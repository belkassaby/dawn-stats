<?php header('Access-Control-Allow-Origin: *'); ?>
<?php

require 'config.php';

$server = mysql_connect ( $host, $user, $password );
$connection = mysql_select_db ( $database, $server );

// type of query to run : can be "modules", "views", "perspectives", "java", "geo", "os"
$type = $_GET ["type"];
switch ($type) {
	case "modules" :
		$myquery = "SELECT date_format(time, '%b %Y') as date, count(*) as total_logins, count(distinct(user_id)) as unique_logins
FROM dawnstats.moduleload_record
group by date
order by id;";
		break;
	case "views" :
		$myquery = "SELECT description, count(*)
as number
FROM dawnstats.usagedata_record 
where kind='view' 
group by description
order by description;";
		break;
	case "perspectives" :
		$myquery = "SELECT description, count(*)
as number
FROM dawnstats.usagedata_record 
where kind='perspective' 
group by description
order by description;";
		break;
	case "java" :
		$myquery = "SELECT description as 'key', count(*) as y 
FROM dawnstats.usagedata_record
where kind='sysinfo'
and what='java.version'
group by description
order by description;";
		break;
	case "geo" :
		$myquery = "SELECT description as 'key', count(*) as y 
FROM dawnstats.usagedata_record
where kind='sysinfo'
and what='locale'
group by description
order by description;";
		break;
	case "os" :
		$myquery = "SELECT description as 'key', count(*) as y 
FROM dawnstats.usagedata_record
where kind='sysinfo'
and what='os'
group by description
order by description;";
		break;
}

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