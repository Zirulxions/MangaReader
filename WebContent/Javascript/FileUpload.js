function $(id){
	return document.getElementById(id);
}

var xhr = new XMLHttpRequest();
var File = "";
function upload () {
	var fData = new FormData();
	fData.append("file", $("file").files[0]);
	fData.append("mangaName", $("mangaName").value);
	File = $("file").files[0].name;
	//xhr.onreadystatechange = function () {
	//	if (xhr.status === 200 && xhr.readyState === 4) {
	//		$("uploadStatus").textContent = xhr.responseText + "\nFile uploaded";
	//	}
	//}
	xhr.open("POST", "./FileManager", true);	
	xhr.send(fData);
}

function forceLower(strInput) {
	strInput.value=strInput.value.toLowerCase();
}

function checkName(){
	if($("mangaName").value != "" || $("mangaName").style.display === "block"){
		$("button4").disabled = false;
	} else {
		$("mangaName").value = "";
		$("button4").disabled = true;
	}
}

function SwitchURL(){
	history.replaceState({},"Upload New File", "Th3.PaSw0rD.15.ParaleL3piped0.html");
	$("button4").disabled = true;
    $("mangaName").style.display = "none";
}

function validateFileType(){
    var fileName = $("file").value;
    var idxDot = fileName.lastIndexOf(".") + 1;
    var extFile = fileName.substr(idxDot, fileName.length).toLowerCase();
    if (extFile=="jpg" || extFile=="jpeg" || extFile=="png"){
        $("mangaName").style.display = "block";
    }else{
    	$("mangaName").style.display = "none";
        alert("Only jpg/jpeg and png files are allowed!");
    }   
}

//Up.jja.o.kajdks.FF.HOy76rfJ.i.AQWtxfshg.LE.HALO4ROCKS.