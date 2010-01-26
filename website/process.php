<?PHP

 require('common.php');

 define('VERSION', 1);

/*
| record_id      | int(5)      | NO   | PRI | NULL    | auto_increment | 
| record_ip      | varchar(15) | YES  |     | NULL    |                | 
| record_headers | text        | YES  |     | NULL    |                | 
| record_data    | text        | YES  |     | NULL    |                | 
*/

/*
| log_id         | int(5)       | NO   | PRI | NULL    | auto_increment | 
| log_ip         | varchar(15)  | YES  |     | NULL    |                | 
| log_imei       | varchar(16)  | YES  |     | NULL    |                | 
| log_activity   | varchar(100) | YES  |     | NULL    |                | 
| log_version    | varchar(10)  | YES  |     | NULL    |                | 
| log_time       | int(15)      | YES  |     | NULL    |                | 
| log_statuscode | int(5)       | YES  |     | NULL    |                | 
| log_pversion   | int(5)       | YES  |     | NULL    |                | 
| log_headers    | text         | YES  |     | NULL    |                | 
| log_data       | text         | YES  |     | NULL    |                | 
*/

 $sql = 'SELECT record_id, record_ip, record_headers, record_data FROM unprocessed';
 $res = mysql_query($sql);

 while ($row = mysql_fetch_assoc($res)) {
  $ip = $row['record_ip'];

  $headers = array();

  foreach (explode("\n", $row['record_headers']) as $line) {
   if (preg_match('/(.*?): (.*)$/', $line, $m)) {
    $headers[$m[1]] = $m[2];
   }
  }

  $imei = isset($headers['IMEI']) ? $headers['IMEI'] : '';
  $activity = isset($headers['ACTIVITY']) ? $headers['ACTIVITY'] : '';
  $version = isset($headers['VERSION']) ? $headers['VERSION'] : '';
  
  if (preg_match('/^([0-9]+)[0-9]{3}:.*/', $row['record_data'], $m)) {
   $time = (int) $m[1];
  } else {
   $time = 0;
  }

  if (empty($imei)) {
   $statuscode = 2;
  } else if (empty($activity) || $activity == '<Unknown>') {
   $statuscode = 3;
  } else if (empty($version)) {
   $statuscode = 4;
  } else if (empty($row['record_data'])) {
   $statuscode = 5;
  } else if ($time == 0 || date('Y', $time) < 2010) {
   $statuscode = 6;
  } else if (count(explode("\n", $row['record_data'])) < 50) {
   $statuscode = 7;
  } else {
   $statuscode = 1;
  }

  $pversion = VERSION;
  $headers = $row['record_headers'];
  $data = $row['record_data'];

  $sql  = 'INSERT INTO sensorlogger (log_ip, log_imei, log_activity, log_version, ';
  $sql .= 'log_time, log_statuscode, log_pversion, log_headers, log_data) VALUES (';
  $sql .= '\'' . m($ip) . '\', \'' . m($imei) . '\', \'' . m($activity) . '\', \'';
  $sql .= m($version) . '\', ' . ((int) $time) . ', ' . ((int) $statuscode) . ', ';
  $sql .= ((int) $pversion) . ', \'' . m($headers) . '\', \'' . m($data) . '\')';
  mysql_query($sql) or die(mysql_error());

  $sql  = 'DELETE FROM unprocessed WHERE record_id = ' . $row['record_id'];
  mysql_query($sql);
 }

?>
