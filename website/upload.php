<?PHP

 require_once('common.php');

 $headers = '';

 foreach ($_SERVER as $k => $v) {
  if (substr($k, 0, 7) == 'HTTP_X_') {
   $headers .= substr($k, 7) . ': ' . $v . "\n";
  }
 }

 $sql  = 'INSERT INTO unprocessed (record_ip, record_headers, record_data) VALUES (';
 $sql .= '\'' . m($_SERVER['REMOTE_ADDR']) . '\', ';
 $sql .= '\'' . m($headers) . '\', ';
 $sql .= '\'' . m(file_get_contents('php://input')) . '\')';

 mysql_query($sql) or die('Error: ' . mysql_error() . '<br>'. $sql);

 Oblong("\002[ANDROID]\002 New data uploaded: " . implode('; ', explode("\n", $headers)));

 require_once('process.php');

?>
