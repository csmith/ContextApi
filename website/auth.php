<?PHP

 session_start();

 define('OPENID_TRUSTROOT', 'http://chris.smith.name/android/');

 $allowedIDs = array(
        'https://www.google.com/accounts/o8/id?id=AItOawlVzlhRypZuhvENeUFtVWY_flhmyihDje8', // Me
        'https://www.google.com/accounts/o8/id?id=AItOawkmbwj0T1NMwv9GVrrY5g3VD5WsiVlWUhc', // Simon
        'https://www.google.com/accounts/o8/id?id=AItOawkZ2rNlFp4lHowKWFYFiGRYLi51dlrhAzI', // Shane
 );

 if (isset($_SESSION['openid']['error'])) {

  // Failed OpenID login attempt
  echo 'ERROR: ',  htmlentities($_SESSION['openid']['error']);
  unset($_SESSION['openid']['error']);
  exit;

 } else if (isset($_SESSION['openid']['validated']) && $_SESSION['openid']['validated']) {

  if (!in_array($_SESSION['openid']['identity'], $allowedIDs)) {
   echo 'ERROR: ', htmlentities($_SESSION['openid']['identity']), ' not permitted';
   unset($_SESSION['openid']);
   exit;
  }

 } else {

  if (!isset($_REQUEST['openid_mode'])) {
   $_POST['openid_url'] = 'https://www.google.com/accounts/o8/id';
  }

  require('openid/processor.php');
  exit;

 }

?>
