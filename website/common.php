<?PHP

 require_once('settings.php');

 mysql_connect(MYSQL_HOST, MYSQL_USER, MYSQL_PASS);
 mysql_select_db(MYSQL_DB);

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

?>
