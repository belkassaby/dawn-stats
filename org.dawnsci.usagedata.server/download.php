<?php header('Access-Control-Allow-Origin: *'); ?>
<?php

require 'config.php';

$server = mysql_connect ( $host, $user, $password );
$connection = mysql_select_db ( $database, $server );

// type of query to run : can be "modules", "views", "perspectives", "java", "geo", "os"
$myquery = "SELECT date_format(time, '%b %Y') as date, count(*) as total_logins, count(distinct(user_id)) as unique_logins
FROM dawnstats.moduleload_record
group by date
order by id;";

$query = mysql_query ( $myquery );

if (! $query) {
	echo mysql_error ();
	die ();
}

$data = array ();

for($x = 0; $x < mysql_num_rows ( $query ); $x ++) {
	$data [] = mysql_fetch_assoc ( $query );
}

echo json_encode ( $data );

mysql_close ( $server );
?>