<?PHP

 require('common.php');

 if (isset($_GET['graph'])) {
  $imei = isset($_GET['imei']) ? $_GET['imei'] : decodeIMEI($_GET['code']);

  $sql = 'SELECT log_data FROM sensorlogger WHERE log_id = ' . ((int) $_GET['graph']) . ' AND log_imei = \'' . m($imei) . '\'';
  $res = mysql_query($sql);

  if (mysql_num_rows($res) == 0) {
   die('Data not found');
  }

  $row = mysql_fetch_assoc($res);

  $datax = array();
  $datay = array();
  $dataz = array();
  foreach (explode("\n", $row['log_data']) as $line) {
   if (preg_match('/([0-9]+):(?:.*?,.*?,.*?,){'.($_GET['ds']-1).'}([0-9\.\-]+),([0-9\.\-]+),([0-9\.\-]+)/', $line, $m)) {
    $datax[] = (double) $m[2];
    $datay[] = (double) $m[3];
    $dataz[] = (double) $m[4];
   }
  }
  
  $im = imagecreatetruecolor(count($datax) * 2, 330);
  $w = imagecolorallocate($im, 255, 255, 255);
  imagefill($im, 0, 0, $w);
 
  $r = imagecolorallocate($im, 255, 0, 0);
  $g = imagecolorallocate($im, 0, 255, 0);
  $b = imagecolorallocate($im, 0, 0, 255);

  $grey = imagecolorallocate($im, 200, 200, 200);
  $lgrey = imagecolorallocate($im, 230, 230, 230);
 
  $minx = min($datax); $maxx = max($datax); $rangex = max(0.01, $maxx - $minx);
  $miny = min($datay); $maxy = max($datay); $rangey = max(0.01, $maxy - $miny);
  $minz = min($dataz); $maxz = max($dataz); $rangez = max(0.01, $maxz - $minz);
  $lastx = $lasty = $lastz = -0;

  imagestring($im, 1, 2, 6, 'Min: ' . $minx . '; Max: ' . $maxx . '; Range: ' . $rangex, $grey);
  imagestring($im, 1, 2, 116, 'Min: ' . $miny . '; Max: ' . $maxy . '; Range: ' . $rangey, $grey);
  imagestring($im, 1, 2, 226, 'Min: ' . $minz . '; Max: ' . $maxz . '; Range: ' . $rangez, $grey);

  imageline($im, 0, 5, count($datax) * 2, 5, $grey);
  imageline($im, 0, 105, count($datax) * 2, 105, $grey);
  imageline($im, 0, 115, count($datax) * 2, 115, $grey);
  imageline($im, 0, 215, count($datax) * 2, 215, $grey);
  imageline($im, 0, 225, count($datax) * 2, 225, $grey);
  imageline($im, 0, 325, count($datax) * 2, 325, $grey);

  for ($i = 0; $i < count($datax); $i++) {
   $x = ($datax[$i] - $minx) * 100 / $rangex + 5;
   $y = ($datay[$i] - $miny) * 100 / $rangey + 115;
   $z = ($dataz[$i] - $minz) * 100 / $rangez + 225;

   if ($i > 0) {
    imageline($im, 2 * $i - 2, $lastx, $i * 2, $x, $r);
    imageline($im, 2 * $i - 2, $lasty, $i * 2, $y, $g);
    imageline($im, 2 * $i - 2, $lastz, $i * 2, $z, $b);
   }

   $lastx = $x; $lasty = $y; $lastz = $z;
  }

  header('Content-type: image/png');
  imagecolortransparent($im, $w); 
  imagepng($im);
  return;
 }

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
