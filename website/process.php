<?PHP

 require_once('common.php');

 define('VERSION', 25);

 function processExceptions($records = true) {
  $sql = 'SELECT record_id, record_ip, record_headers, record_data FROM unprocessed';
  $res = mysql_query($sql);
  $count = 0;

  while ($row = mysql_fetch_assoc($res)) {
   $ip = $row['record_ip'];

   $headers = array();

   foreach (explode("\n", $row['record_headers']) as $line) {
    if (preg_match('/(.*?): (.*)$/', $line, $m)) {
     $headers[$m[1]] = $m[2];
    }
   }

   if (!isset($headers['APPLICATION']) || substr($headers['APPLICATION'], -10) != '-exception') {
    continue;
   }

   $application = substr($headers['APPLICATION'], 0, -10);
   $imei = isset($headers['IMEI']) ? $headers['IMEI'] : '';

   if (!ctype_digit($imei) && !empty($imei)) {
    // It's probably an MEID not an IMEI number
    $imei = bchexdec($headers['IMEI']);
   }

   $version = isset($headers['VERSION']) ? $headers['VERSION'] : '';
   $headers = $row['record_headers'];
   $data = $row['record_data'];

   $sql  = 'INSERT INTO exceptions (ex_ip, ex_imei, ex_application, ex_version, ';
   $sql .= 'ex_headers, ex_trace) VALUES (';
   $sql .= '\'' . m($ip) . '\', \'' . m($imei) . '\', \'' . m($application) . '\', \'';
   $sql .= m($version) . '\', \'' . m($headers) . '\', \'' . m($data) . '\')';
   mysql_query($sql) or die(mysql_error());

   $sql  = 'DELETE FROM unprocessed WHERE record_id = ' . $row['record_id'];
   mysql_query($sql);

   $count++;
  }

  if ($count > 1 || !$records && $count > 0) {
   Oblong("\002[ANDROID]\002 Processed $count " . ($records ? "new" : "existing") . " exceptions");
  }
 }

 function processSensorLogger($records = true) {
  $sql = $records ? 'SELECT record_id, record_ip, record_headers, record_data FROM unprocessed'
                  : 'SELECT log_id, log_ip AS record_ip, log_headers AS record_headers, log_data '
		  . 'AS record_data FROM sensorlogger WHERE log_pversion < ' . VERSION;
  $res = mysql_query($sql);
  $count = 0;
  $codes = array();

  while ($row = mysql_fetch_assoc($res)) {
   $ip = $row['record_ip'];

   $headers = array();

   foreach (explode("\n", $row['record_headers']) as $line) {
    if (preg_match('/(.*?): (.*)$/', $line, $m)) {
     $headers[$m[1]] = $m[2];
    }
   }

   if (!isset($headers['APPLICATION']) || $headers['APPLICATION'] != 'SensorLogger') {
    continue;
   }

   $imei = isset($headers['IMEI']) ? $headers['IMEI'] : '';

   if (!ctype_digit($imei) && !empty($imei)) {
    // It's probably an MEID not an IMEI number
    $imei = bchexdec($headers['IMEI']);
   }

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
   } else if (count(explode("\n", $row['record_data'])) < 500) {
    $statuscode = 7;
   } else {
    // Check for duplicates
    $sql2  = 'SELECT COUNT(*) FROM sensorlogger WHERE LEFT(log_data, ' . strlen($row['record_data']) . ')';
    $sql2 .= ' = LEFT(\'' . m($row['record_data']) . '\', ' . strlen($row['record_data']) . ')';

    if (!$record) {
     $sql2 .= ' AND (log_id < ' . $row['log_id'] . ' OR LENGTH(log_data) > ' . strlen($row['record_data']) . ')';
    }

    $res2  = mysql_query($sql2);
    $num2 = (int) mysql_result($res2, 0);

    if ($num2 > 0) {
     $statuscode = 8;
    } else {
     $error = false;

     // Check for repeated data
     $last = array(); $lastcount = array();
     foreach (explode("\n", $row['record_data']) as $line) {
      $bits = explode(',', trim(substr($line, strpos($line, ':'))));
      foreach ($bits as $o => $bit) {
       if (empty($bit)) { continue; }
       if ($last[$o] == $bit) { $lastcount[$o]++; } else { $lastcount[$o] = 0; }
       $last[$o] = $bit;
      }

      if (max($lastcount) > 200) {
       $error = true;
       break;
      }
     }

     $statuscode = $error ? 9 : 1;
    }
   }

   $codes[$statuscode]++;

   $pversion = VERSION;
   $headers = $row['record_headers'];
   $data = $row['record_data'];

   if ($records) {
    $sql  = 'INSERT INTO sensorlogger (log_ip, log_imei, log_activity, log_version, ';
    $sql .= 'log_time, log_statuscode, log_pversion, log_headers, log_data) VALUES (';
    $sql .= '\'' . m($ip) . '\', \'' . m($imei) . '\', \'' . m($activity) . '\', \'';
    $sql .= m($version) . '\', ' . ((int) $time) . ', ' . ((int) $statuscode) . ', ';
    $sql .= ((int) $pversion) . ', \'' . m($headers) . '\', \'' . m($data) . '\')';
    mysql_query($sql) or die(mysql_error());

    $sql  = 'DELETE FROM unprocessed WHERE record_id = ' . $row['record_id'];
    mysql_query($sql);
   } else {
    $sql  = 'UPDATE sensorlogger SET log_ip = \'' . m($ip) . '\', log_imei = \'';
    $sql .= m($imei) . '\', log_activity = \'' . m($activity) . '\', log_version = \'';
    $sql .= m($version) . '\', log_time = ' . ((int) $time) . ', log_statuscode = ';
    $sql .= ((int) $statuscode) . ', log_pversion = ' . ((int) $pversion) . ', log_headers = \'';
    $sql .= m($headers) . '\', log_data = \'' . m($data) . '\' WHERE log_id = ' . $row['log_id'];
    mysql_query($sql);
   }

   $count++;
  }

  if ($count > 1 || !$records && $count > 0) {
   $codestr = '';
   asort($codes);

   foreach ($codes as $code => $count) {
    $codestr .= (!empty($codestr) ? '; ' : '') . $code . ': ' . $count;
   }

   Oblong("\002[ANDROID]\002 Processed $count " . ($records ? "new" : "existing") . " SensorLogger dataset(s). Status codes: $codestr");
  }
 }

 processSensorLogger(!isset($argv[1]) || $argv[1] != '--update');
 processExceptions(!isset($argv[1]) || $argv[1] != '--update');

?>
