function $(id){
	return document.getElementById(id);
}

function upload(){
	var formData = new FormData();
	formData.append("file", document.getElementById("file").files[0]);
	formData.append("mangaName", document.getElementById("mangaName").value);
	formData.append("newChapter", document.getElementById("newChapter").value);
	let config = {
		method: 'POST',
		body: formData,
		header: {'Content-Type':'multipart/form-data'},
	};
	fetch("./FileManager", config)
		.then(function(response){
			return response.json();
		})
		.then(function(formData){
			console.log(formData);
		});
	}

function mdelete(){
	var fData = new FormData();
	fData.append("mangaDelete", document.getElementById("mangaDelete").value);
	fData.append("chapter", document.getElementById("chapter").value);
	let config = {
		method: 'POST',
		body: fData,
		header: {'Content-Type':'multipart/form-data'},
	};
	fetch("./DeleteFile", config)
		.then(function(response){
			return response.json();
		})
		.then(function(fData){
			console.log(fData);
		})
	
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