<?PHP

 require('auth.php');
 require('common.php');

?>
<h1>Exceptions</h1>

<?PHP

 $sql = 'SELECT ex_application, ex_ip, ex_imei, ex_version, ex_trace FROM exceptions ORDER BY ex_application, ex_version';
 $res = mysql_query($sql);

 function fix($x) { return str_replace('.', '/', $x[1]); }

 echo '<table border="1">';

 while ($row = mysql_fetch_assoc($res)) {

  echo '<tr><td><table>';
  foreach ($row as $k => $v) { echo '<tr><th>', $k, '</th><td>', $k == 'ex_trace' ? ($points = count(explode("\n", $v))) . ' line(s)' : nl2br(htmlentities($v)), '</td></tr>'; }

  echo '</table>';

  echo '</td><td><pre>';

  parse_str($row['ex_trace'], $data);
  echo preg_replace_callback('/__(.*?)__/', 'fix', preg_replace('(at (uk\.co\.md87.*?)(\.[^.]+)\((.*?):([0-9]+)\))', 'at \1\2(<a href="http://github.com/csmith/ContextApi/blob/' . $row['ex_application'] . '/' . $row['ex_version'] . '/code/' . $row['ex_application'] . '/src/__\1__.java#L\4">\3:\4</a>)', htmlentities($data['stacktrace'])));

  echo '</pre></td>';
  echo '</tr>';
 }

 echo '</table>';

?>
