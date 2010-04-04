<?PHP

 require('auth.php');
 require('common.php');

 # -------------- Form handling -----------------

 function process_activity_add($args) {
  $sql  = 'INSERT INTO activities (activity_name, activity_parent) VALUES (\'';
  $sql .= m($args['name']) . '\', ' . ((int) $args['parent']) . ')';
  mysql_query($sql);
 }

 function process_activity_delete($args) {
  $sql = 'DELETE FROM windowclassifications WHERE activity_id = ' . ((int) $args['id']);
  mysql_query($sql);

  $sql = 'SELECT activity_id FROM activities WHERE activity_parent = ' . ((int) $args['id']);
  $res = mysql_query($sql);

  while ($row = mysql_fetch_assoc($res)) {
   process_activity_delete(array('id' => $row['activity_id']));
  }

  $sql = 'DELETE FROM activities WHERE activity_id = ' . ((int) $args['id']);
  mysql_query($sql);
 }

 function process_sample_edit($args) {
  $sql = 'SELECT wc_id, activity_id, log_id, wc_offset FROM windowclassifications';
  $res = mysql_query($sql);
  $acs = getActivityArray();

  while ($row = mysql_fetch_assoc($res)) {
   $name = 'value_' . $row['log_id'] . '_' . $row['wc_offset'];
   if (isset($args[$name])) {
    if ((int) $args[$name] == (int) $row['activity_id']) {
     unset($args[$name]);
    } else {
     mysql_query('UPDATE windowclassifications SET activity_id = ' . ((int) $args[$name]) . ' WHERE wc_id = ' . $row['wc_id']);
    }
   }
  }

  foreach ($args as $name => $val) {
   if ($acs[$val] == 'UNCLASSIFIED/PENDING') { continue; }

   list($value, $log, $offset) = explode('_', $name);
   mysql_query('INSERT INTO windowclassifications (activity_id, log_id, wc_offset) VALUES ('. ((int) $val) . ', ' . ((int) $log) . ', ' . ((int) $offset) . ')');
  }
 }

 function process_export($args) {
  $acs = getActivityArray();
  $sql = 'SELECT wc_offset, activity_id, log_data FROM activities NATURAL JOIN windowclassifications NATURAL JOIN sensorlogger WHERE log_statuscode = 1 AND (0';

  foreach ($acs as $id => $name) {
   if (substr($name, 0, 10) == 'CLASSIFIED') {
    $sql .= ' OR activity_id = ' . $id;
   }
  }

  $sql .= ')';
  $res  = mysql_query($sql);

  header('Content-type: text/plain');
  header('Content-disposition: attachment; filename=data.txt');

  while ($row = mysql_fetch_assoc($res)) {
   echo 'Activity: ', $acs[$row['activity_id']], "\n";
   $data = array_slice(explode("\n", $row['log_data']), $row['wc_offset'], 128);
   echo implode("\n", $data);
   echo "\n";
  }

  exit;
 }

 if (isset($_POST['action'])) {
  $args = array();
  $action = str_replace('.', '_', $_POST['action']) . '_';
  foreach ($_POST as $k => $v) {
   if (substr($k, 0, strlen($action)) == $action) {
    $args[substr($k, strlen($action))] = $v;
   }
  }

  call_user_func('process_' . str_replace('.', '_', $_POST['action']), $args);
  header('Location: /android/admin.php');
  exit;
 }

 # ------------------- End of form handling ----------------------

 $acs = getActivityArray();

?>
<h1>Activity management</h1>

<h2>Add an activity</h2>

<form action="admin.php" method="post">
 <input type="hidden" name="action" value="activity.add">
 <select name="activity.add.parent">
<?PHP
 asort($acs);

 foreach ($acs as $id => $name) {
  echo ' <option value="', $id, '">', htmlentities($name), '</option>';
 }
?>
 </select> / 
 <input type="text" name="activity.add.name">
 <input type="submit" value="Add">
</form>

<h2>Delete an activity</h2>

<form action="admin.php" method="post">
 <input type="hidden" name="action" value="activity.delete">
 <select name="activity.delete.id">
<?PHP
 asort($acs);

 foreach ($acs as $id => $name) {
  echo ' <option value="', $id, '">', htmlentities($name), '</option>';
 }
?>
 <input type="submit" value="Delete">
</form>

<h1>Export</h1>

<form action="admin.php" method="post">
 <input type="hidden" name="action" value="export">
 <input type="submit" value="Export classified windows">
</form>

<h1>Sample management</h1>
<?PHP

 $sql = 'SELECT log_id, log_imei, log_version, log_time, log_activity, log_headers, log_data FROM sensorlogger WHERE log_statuscode = 1';
 $res = mysql_query($sql);

?>

<style type="text/css">
  .windowed { background: url('windowbg.png') repeat-y -256px 0px; }
  .windowboxes { margin: 0px; padding: 0px; border-right: 1px solid black; display: inline-block; }
  .windowboxes li { display: inline-block; width: 255px; text-align: center; border: 1px solid black; margin: 0px; padding: 0px; border-right: 0; }
  .windowboxes.odd { padding-left: 128px; }
  .windowboxes li select { width: 253px; }
</style>
<script type="text/javascript">
  function showWindow(id, offset) {
   document.getElementById('window_' + id).style.backgroundPosition = (offset * 2) + "px 0px";
  }

  function hideWindow(id) {
   showWindow(id, -128);
  }

  function classifyAll(select) {
   var tr = select.parentNode.parentNode;
   var selects = tr.getElementsByTagName('select');

   for (var i = 0; i < selects.length; i++) {
    selects[i].value = select.value;
   }
  }
</script>
<form action="admin.php" method="post">
 <input type="hidden" name="action" value="sample.edit">
<?PHP

 echo '<table border="1">';

 while ($row = mysql_fetch_assoc($res)) {

  $sql2 = 'SELECT wc_offset, activity_id FROM windowclassifications WHERE log_id = ' . $row['log_id'];
  $res2 = mysql_query($sql2);
  $wcs = array();
  
  while ($row2 = mysql_fetch_assoc($res2)) {
   $wcs[(int) $row2['wc_offset']] = (int) $row2['activity_id'];
  }

  $points = 0;

  echo '<tr><td><table>';
  foreach ($row as $k => $v) { echo '<tr><th>', $k, '</th><td>', $k == 'log_data' ? ($points = count(explode("\n", $v))) . ' line(s)' : nl2br(htmlentities($v)), '</td></tr>'; }

  echo '</table>';

  echo '<select onChange="classifyAll(this)">';
  echo ' <option value="">Classify all as...</option>';
   foreach ($acs as $id => $name) {
    echo '<option value="', $id, '">', htmlentities($name), '</option>';
   }
  echo '</select>';

  echo '</td><td>';

  echo '<ol class="windowboxes even">';
  for ($i = 0; $i + 128 < $points; $i += 128) {
   echo '<li onMouseOver="showWindow(', $row['log_id'], ', ', $i, ')" onMouseOut="hideWindow(', $row['log_id'], ')">';
   echo '<select name="sample.edit.value_', $row['log_id'], '_', $i, '">';
   foreach ($acs as $id => $name) {
    echo '<option value="', $id, '"', (isset($wcs[$i]) && $wcs[$i] == $id) || (!isset($wcs[$i]) && $name == 'UNCLASSIFIED/PENDING') ? ' selected="selected"' : '','>', htmlentities($name), '</option>';
   }
   echo '</select>';
   echo '</li>';
  }
  echo '</ol>';

  echo '<div class="windowed" id="window_', $row['log_id'], '" styleb"background-color: orange;">';
  echo '<img src="data.php?graph=', $row['log_id'], '&amp;ds=1&amp;imei=', $row['log_imei'], '" height="330">';
  echo '<br><img src="data.php?graph=', $row['log_id'], '&amp;ds=2&amp;imei=', $row['log_imei'], '" height="330">';
  echo '</div>';

  echo '<ol class="windowboxes odd">';
  for ($i = 64; $i + 128 < $points; $i += 128) {
   echo '<li onMouseOver="showWindow(', $row['log_id'], ', ', $i, ')" onMouseOut="hideWindow(', $row['log_id'], ')">';
   echo '<select name="sample.edit.value_', $row['log_id'], '_', $i, '">';
   foreach ($acs as $id => $name) {
    echo '<option value="', $id, '"', (isset($wcs[$i]) && $wcs[$i] == $id) || (!isset($wcs[$i]) && $name == 'UNCLASSIFIED/PENDING') ? ' selected="selected"' : '','>', htmlentities($name), '</option>';
   }
   echo '</select>';
   echo '</li>';
  }
  echo '</ol>';

  echo '</td>';
  echo '</tr>';
 }

 echo '</table>';

?>
 <input type="submit" value="SUBMIT ALL MODIFICATIONS">
</form>
