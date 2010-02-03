<?PHP

 session_start();

 $allowedIDs = array(
	'https://www.google.com/accounts/o8/id?id=AItOawk9b32oBoVvjzwEhvC2GOhsxj0MN2mWoc8', // Me
	'https://www.google.com/accounts/o8/id?id=AItOawk8LzSeazeZxqMCTVKm-OkUu0mLDLOqBBs', // Simon
 );

 if (isset($_SESSION['openid']['error'])) {

  // Failed OpenID login attempt
  echo 'ERROR: ',  htmlentities($_SESSION['openid']['error']);
  unset($_SESSION['openid']['error']);
  exit;

 } else if (isset($_SESSION['openid']['validated']) && $_SESSION['openid']['validated']) {

  if (!in_array($_SESSION['openid']['identity'], $allowedIDs)) {
   echo 'ERROR: ', htmlentities($_SESSION['openid']['identity']), ' not permitted';
   exit;
  }

 } else {

  if (!isset($_REQUEST['openid_mode'])) {
   $_POST['openid_url'] = 'https://www.google.com/accounts/o8/id';
  }

  require('openid/processor.php');
  exit;

 }

 # -------------- End of authentication code --------------------------

 require('common.php');

 # -------------- Form handling -----------------

 function process_activity_add($args) {
  $sql  = 'INSERT INTO activities (activity_name, activity_parent) VALUES (\'';
  $sql .= m($args['name']) . '\', ' . ((int) $args['parent']) . ')';
  mysql_query($sql);
 }

 if (isset($_POST['action'])) {
  $args = array();
  $action = str_replace('.', '_', $_POST['action']) . '_';
  foreach ($_POST as $k => $v) {
   if (substr($k, 0, strlen($action)) == $action) {
    $args[substr($k, strlen($action))] = $v;
   }
  }

  call_user_func('process_' . str_replace('.', '_', $_POST['action']), $args);
  header('Location: /android/admin.php');
  exit;
 }

 # ------------------- End of form handling ----------------------

 $acs = getActivityArray();

?>
<h1>Activity management</h1>

<h2>Add an activity</h2>

<form action="admin.php" method="post">
 <input type="hidden" name="action" value="activity.add">
 <select name="activity.add.parent">
<?PHP
 asort($acs);

 foreach ($acs as $id => $name) {
  echo ' <option value="', $id, '">', htmlentities($name), '</option>';
 }
?>
 </select> / 
 <input type="text" name="activity.add.name">
 <input type="submit" value="Add">
</form>

<h1>Sample management</h1>
<?PHP

 $sql = 'SELECT log_id, log_imei, log_version, log_time, log_activity, log_data FROM sensorlogger WHERE log_statuscode = 1';
 $res = mysql_query($sql);

?>

<style type="text/css">
  .windowed { background: url('windowbg.png') repeat-y -256px 0px; }
  .windowboxes { margin: 0px; padding: 0px; border-right: 1px solid black; display: inline-block; }
  .windowboxes li { display: inline-block; width: 255px; text-align: center; border: 1px solid black; margin: 0px; padding: 0px; border-right: 0; }
  .windowboxes.odd { padding-left: 128px; }
</style>
<script type="text/javascript">
  function showWindow(id, offset) {
   document.getElementById('window_' + id).style.backgroundPosition = (offset * 2) + "px 0px";
  }

  function hideWindow(id) {
   showWindow(id, -128);
  }
</script>

<?PHP

 echo '<table border="1">';
 $first = true;

 while ($row = mysql_fetch_assoc($res)) {
  if ($first) {
   echo '<tr>';
   foreach ($row as $k => $v) { echo '<th>', $k, '</th>'; }
   echo '</tr>';
   $first = false;
  }

  $points = 0;

  echo '<tr>';
  foreach ($row as $k => $v) { echo '<td>', $k == 'log_data' ? ($points = count(explode("\n", $v))) . ' line(s)' : nl2br(htmlentities($v)), '</td>'; }

  echo '<td>';

  echo '<ol class="windowboxes even">';
  for ($i = 0; $i + 128 < $points; $i += 128) {
   echo '<li onMouseOver="showWindow(', $row['log_id'], ', ', $i, ')" onMouseOut="hideWindow(', $row['log_id'], ')">Window</li>';
  }
  echo '</ol>';

  echo '<div class="windowed" id="window_', $row['log_id'], '" styleb"background-color: orange;">';
  echo '<img src="data.php?graph=', $row['log_id'], '&amp;ds=1&amp;imei=', $row['log_imei'], '" height="330">';
  echo '<br><img src="data.php?graph=', $row['log_id'], '&amp;ds=2&amp;imei=', $row['log_imei'], '" height="330">';
  echo '</div>';

  echo '<ol class="windowboxes odd">';
  for ($i = 64; $i + 128 < $points; $i += 128) {
   echo '<li onMouseOver="showWindow(', $row['log_id'], ', ', $i, ')" onMouseOut="hideWindow(', $row['log_id'], ')">Window</li>';
  }
  echo '</ol>';

  echo '</td>';
  echo '</tr>';
 }

 echo '</table>';

?>
