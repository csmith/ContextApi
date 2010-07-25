<?PHP

 require('auth.php');
 require('common.php');

?>
<h1>Export</h1>

<?PHP

 $sql = 'SELECT log_headers, log_data FROM sensorlogger';
 $res = mysql_query($sql);

 $fh = fopen('compress.bzip2://' . dirname(dirname(__FILE__)) . '/res/data.sql.bz2', 'w'); 

 $users = array();

 while ($row = mysql_fetch_assoc($res)) {
  preg_match('/IMEI: (.*?)$/m', $row['log_headers'], $m);
  if (!isset($users[$m[1]])) {
   $users[$m[1]] = count($users);
  }
  $row['log_headers'] = str_replace($m[0], 'USER: ' . $users[$m[1]], $row['log_headers']);

  fputs($fh, "INSERT INTO sensorlogger (log_headers, log_data) VALUES ('" . mysql_real_escape_string($row['log_headers']) . "', '" . mysql_real_escape_string($row['log_data']) . "')\n");
 }

 fclose($fh);

 echo 'Wrote ', filesize(dirname(dirname(__FILE__)) . '/res/data.sql.bz2'), ' Bytes.';
?>
