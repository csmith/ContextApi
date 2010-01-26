<?PHP

 mysql_connect('localhost', 'md87_android', 'android73274');
 mysql_select_db('md87_android');

 function Oblong($message) {
        $key = trim(file_get_contents('/home/chris/oblong.key'));
        $chan = '#MD87';

        $fp = @fsockopen("oblong.md87.co.uk", 3302, $errno, $errstr, 30);
        if ($fp) {
                $out = $key.' '.$chan.' '.substr($message, 0, 460)."\n";
                fwrite($fp, $out);
                fclose($fp);
        }
 }

?>
