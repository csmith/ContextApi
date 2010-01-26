<?PHP

 require('common.php');

 $headers = '';

 foreach ($_SERVER as $k => $v) {
  if (substr($k, 0, 7) == 'HTTP_X_') {
   $headers .= substr($k, 7) . ': ' . $v . "\n";
  }
 }

 $sql  = 'INSERT INTO unprocessed (record_ip, record_headers, record_data) VALUES (';
 $sql .= '\'' . mysql_real_escape_string($_SERVER['REMOTE_ADDR']) . '\', ';
 $sql .= '\'' . mysql_real_escape_string($headers) . '\', ';
 $sql .= '\'' . mysql_real_escape_string(file_get_contents('php://input')) . '\')';

 mysql_query($sql) or die('Error: ' . mysql_error() . '<br>'. $sql);

?>
