<?PHP

 require_once('common.php');

 if (isset($_GET['code'])) {
  define('CODE', htmlentities($_GET['code']));
  define('IMEI', $_GET['imei'] = decodeIMEI($_GET['code'])); 
 } else {
  die('Invalid code specified');
 }

 $sql = 'SELECT log_activity, log_id, log_statuscode FROM sensorlogger WHERE log_imei = \'' . m(IMEI) . '\'';
 $res = mysql_query($sql);

 echo '<?xml version="1.0"?>';
?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>SensorLogger Results</title>
    <link rel="stylesheet" type="text/css" href="/style.css"/>
    <style type="text/css">
     .qr { float: left; clear: left; margin-right: 15px; }
     .qr img { width: 128px; }
    </style>
  </head>
  <body style="margin-top: 0; padding-top: 0;">
    <h1 style="background: url('/android/android.png') no-repeat right; padding-top: 50px; margin-top: 0;">SensorLogger results for <span><?PHP echo IMEI; ?></span></h1>
    <div>
<?PHP if (($num = mysql_num_rows($res)) == 0) { ?>
     <h2>Welcome</h2>
     <p>
      You do not appear to have submitted any data from the sensor logger application, yet.
     </p>
     <p>
      Why not <a href="/android/">download it</a> and give it a try, then check back soon!
     </p> 
<?PHP } else { ?>
     <h2>Thank you</h2>
     <p>
      You've submitted <strong><?PHP echo number_format($num); ?></strong>
      record<?PHP echo $num == 1 ? '' : 's'; ?> from the sensor logger application.
     </p>
<?PHP
 $data = array();
 $codes = array();
 $rejects = 0;
 
 while ($row = mysql_fetch_assoc($res)) {
  if ($row['log_statuscode'] == '1') {
   $data[] = $row;
  } else {
   $codes[(int) $row['log_statuscode']]++;
   $rejects++;
  }
 }

 if ($rejects > 0) {
?>
   <p>
    Unfortunately, some of these traces have been rejected as they are not suitable for analysis:
   </p>
   <ul>
<?PHP
 foreach ($codes as $code => $count) {
  echo '<li>', number_format($count), ' rejected because ' . getStatusCodeReason($code) . '</li>';
 }
?>
   </ul>
<?PHP if ($num > $rejects) { ?>
  <p>The remaining <strong><?PHP echo $num - $rejects; ?></strong> record<?PHP echo $num > $rejects + 1 ? 's look' : ' looks'; ?>
     good, and <?PHP echo $num > $rejects + 1 ? 'are' : 'is'; ?> shown below:</p>
<?PHP } else { ?>
  <p>Oh dear, that's all of your records. Make sure you're using the <a href="/android/">latest version</a>
     to ensure you get the best results. Record some more and check back soon!</p>
<?PHP }
 } else {
?>
 <p>Our system didn't find any problems with your records, so all of them are shown below:</p>
<?PHP
 }

 if ($rejects < $num) {
?>
  <h2>Your records</h2>
<?PHP

 $acs = getActivityArray();

 foreach ($data as $datum) {
  echo '<h3>', htmlentities($datum['log_activity']), '</h3>';

  $sql = 'SELECT activity_id AS name, COUNT(*) AS num FROM windowclassifications WHERE log_id = ' . $datum['log_id'] . ' GROUP BY activity_id';
  $res = mysql_query($sql);
  $cls = array();

  while ($row = mysql_fetch_assoc($res)) {
   $cls[$row['name']] = $row['num'];
  }

  if (!empty($cls)) {
   echo '<p>This sample has been manually classified into the following activities: ';

   $first = true;
   foreach ($cls as $id => $count) {
    if ($first) { $first = false; } else { echo '; '; }
    echo $acs[$id], ' (', $count, ' window', $count == 1 ? '' : 's', ')';
   }

   echo '</p>';
  }

  echo '<div style="height: 700px; overflow: auto;">';
  echo '<img src="/android/g/', CODE, '/', $datum['log_id'], '/1" height="330" alt="Graph of accelerometer data"/><br/>';
  echo '<img src="/android/g/', CODE, '/', $datum['log_id'] . '/2" height="330" alt="Graph of magnetic fielddata"/>';
  echo '</div>';
 }
?>
 <h2>Check back soon...</h2>
 <p>for information on how your records are being used, explanations of the data, and information on the application
    which will be created as a result</p>
<?PHP } ?>
<?PHP } ?>
    </div>
    <div id="footer"/>
  </body>
</html>
