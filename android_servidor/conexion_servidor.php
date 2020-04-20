<?php
	$H="localhost";
    $U="root";
    $C="root";
    $D="bd_appservidor";

	//recivir objeto/json
	$json=file_get_contents('php://input');
	//decodificarlo
	$data=json_decode($json);
	//obtener los valores del array // -> es para acceder
	$da1=$data->nombre;
	$da2=$data->apellido;
	$da3=$data->edad;
	$da4=$data->radio;

	$bd=mysqli_connect($H, $U, $C, $D);
	if ($bd) {
		echo "Conexion correcta";
		var_dump($bd);
		$sql="INSERT INTO tbl_datosapp(c_nombre,c_apellido,c_edad,c_truefalse) VALUES ('$da1','$da2',$da3,$da4)";
	$result=mysqli_query($bd,$sql);
	//if(mysqli_query($bd,$sql)){
	}else{
		die("Conexion fallida: ".mysqli_connect_error());

	}

?>