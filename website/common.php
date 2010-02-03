<?PHP

 require_once('settings.php');

 mysql_connect(MYSQL_HOST, MYSQL_USER, MYSQL_PASS);
 mysql_select_db(MYSQL_DB);

 function m($sql) {
	return mysql_real_escape_string($sql);
 }

 function bchexdec($hex) {
	$len = strlen($hex);

	for ($i = 1; $i <= $len; $i++) {
		$dec = bcadd($dec, bcmul(strval(hexdec($hex[$i - 1])), bcpow('16', strval($len - $i))));
	}

	return $dec;
 }

 function Oblong($message) {
        $key = trim(file_get_contents('/home/chris/oblong.key'));
        $chan = '#MD87.highvol';

        $fp = @fsockopen("oblong.md87.co.uk", 3302, $errno, $errstr, 30);
        if ($fp) {
                $out = $key.' '.$chan.' '.substr($message, 0, 460)."\n";
                fwrite($fp, $out);
                fclose($fp);
        }
 }

 function getActivityArray() {
	$acs = array();
	$sql = 'SELECT activity_id, activity_name, activity_parent FROM activities ORDER BY activity_parent';
	$res = mysql_query($sql);

	while ($row = mysql_fetch_assoc($res)) {
		$name = $row['activity_name'];

		if (isset($acs[(int) $row['activity_parent']])) {
			$name = $acs[(int) $row['activity_parent']] . '/' . $name;
		}

		$acs[(int) $row['activity_id']] = $name;
	}

	return $acs;
 }

 function getStatusCodeReason($code) {
 	switch ((int) $code) {
		case 2: return 'no IMEI code was specified';
		case 3: return 'no activity was specified';
		case 4: return 'no application version was specified';
		case 5: return 'no sensor data was included';
		case 6: return 'an invalid date was specified';
		case 7: return 'insufficient sensor data was included';
		case 8: return 'the sensor data was already submitted (duplicate)';
		case 9: return 'the sensor data contained long periods of identical values';
	}

	return 'unknown reason';
 }

 function decodeIMEI($input) {
	$chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-=_';
	$tot = '0';
	for ($i = 0; $i < strlen($_GET['code']); $i++) {
		$n = strpos($chars, $_GET['code'][$i]);
		$tot = bcadd($tot, bcmul($n, bcpow(strlen($chars), strlen($_GET['code']) - $i - 1)));
	}
	return $tot;
 }

?>
