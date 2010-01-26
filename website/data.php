<?PHP

 require('common.php');

 if (isset($_GET['graph'])) {
  $sql = 'SELECT record_data FROM unprocessed WHERE record_id = ' . ((int) $_GET['graph']);
  $res = mysql_query($sql);
  $row = mysql_fetch_assoc($res);

  $datax = array();
  $datay = array();
  $dataz = array();
  foreach (explode("\n", $row['record_data']) as $line) {
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

  $minx = min($datax); $maxx = max($datax); $rangex = max(1, $maxx - $minx);
  $miny = min($datay); $maxy = max($datay); $rangey = max(1, $maxy - $miny);
  $minz = min($dataz); $maxz = max($dataz); $rangez = max(1, $maxz - $minz);
  $lastx = $lasty = $lastz = -0;

  for ($i = 0; $i < count($datax); $i++) {
   $x = ($datax[$i] - $minx) * 200 / $rangex;
   $y = ($datay[$i] - $miny) * 200 / $rangey + 100;
   $z = ($dataz[$i] - $minz) * 200 / $rangez + 200;

   if ($i > 0) {
    imageline($im, 2 * $i - 2, $lastx, $i * 2, $x, $r);
    imageline($im, 2 * $i - 2, $lasty, $i * 2, $y, $g);
    imageline($im, 2 * $i - 2, $lastz, $i * 2, $z, $b);
   }

   $lastx = $x; $lasty = $y; $lastz = $z;
  }

  header('Content-type: image/png');
  imagepng($im);
  return;
 }

 $sql = 'SELECT * FROM unprocessed';
 $res = mysql_query($sql);

 echo '<table border="1">';
 $first = true;

 while ($row = mysql_fetch_assoc($res)) {
  if ($first) {
   echo '<tr>';
   foreach ($row as $k => $v) { echo '<th>', $k, '</th>'; }
   echo '</tr>';
   $first = false;
  }

  echo '<tr>';
  foreach ($row as $k => $v) { echo '<td>', $k == 'record_data' ? count(explode("\n", $v)) . ' line(s)' : nl2br(htmlentities($v)), '</td>'; }

  echo '<td>';
  echo '<img src="data.php?graph=', $row['record_id'], '&amp;ds=1"><br>';
  echo '<img src="data.php?graph=', $row['record_id'], '&amp;ds=2">';
  echo '</td>';
  echo '</tr>';
 }

 echo '</table>';

?>
