function $(id){
	return document.getElementById(id);
}

function upload(){
	var formData = new FormData();
	formData.append("mangaName", document.getElementById("mangaName").value);
	formData.append("synopsis", document.getElementById("synopsis").value);
	formData.append("mangaGender", document.getElementById("mangaGender").value);
	let config = {
		method: 'POST',
		body: formData,
		header: {'Content-Type':'multipart/form-data'},
	};
	fetch("./MangaManager", config)
		.then(function(response){
			return response.json();
		})
		.then(function(data){
			alert(data.message);
		});
	}

function upChapter(){
	var fData = new FormData();
	var files = document.getElementById("files").files;
	console.log(files.length);
	fData.append("cMangaName", document.getElementById("cMangaName").value);
	fData.append("chapterNumber", document.getElementById("chapterNumber").value);
	fData.append("chapterTitle", document.getElementById("chapterTitle").value);
	fData.append("chapterPages", document.getElementById("chapterPages").value);
	for (var i = 0; i < files.length - 1; i++) {
		console.log("Added File");
		var file = files[i];
		fData.append("files[]", file, file.name);
	}
	let config = {
		method: 'POST',
		body: fData,
		header: {'Content-Type':'multipart/form-data'},
	};
	fetch("./ChapterManager", config)
		.then(function(response){
			return response.json();
		})
		.then(function(data){
			alert(data.message);
			
		});
	}

function optionDelete(){
	var mangaToDelete = document.getElementById("mangaDelete").value;
	var chapterToDelete = document.getElementById("chapter").value;
	if(mangaToDelete.trim() == ""){
		alert("You need to write the manga name... !");
	} else {
		if(chapterToDelete.trim() == ""){
			console.log("mangaDelete: " + mangaToDelete);
			mdelete();
		} else if(chapterToDelete.trim() != ""){
			console.log("chapter: " + chapterToDelete);
			chDelete();
		}
	}
}

function mdelete(){
	let body = {
		mangaDelete: document.getElementById("mangaDelete").value
	};
	fetch("./MangaManager", {method: "DELETE", body:JSON.stringify(body)})
	    .then(function(resp){
           	return resp.json();
        })
        .then(function(data){
           	console.log(data);
           	if(data.status != null){
            	alert(data.message);
            	if(data.status != null && data.status != undefined){
            		window.location.href = "UploadFile.html";
            	}
           	}
       	});
}

function chDelete(){
	let body = {
		mangaDelete: document.getElementById("mangaDelete").value,
		chapter: document.getElementById("chapter").value
	}
	fetch("./ChapterManager", {method: "DELETE", body:JSON.stringify(body)})
	    .then(function(resp){
           	return resp.json();
        })
        .then(function(data){
           	console.log(data);
           	if(data.status != null){
            	alert(data.message);
            	if(data.status != null && data.status != undefined){
            		window.location.href = "UploadFile.html";
            	}
           	}
       	});
}

function createChapterFiles(){
	var form = new FormData();
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

function setImages(){
	fetch("./ChapterManager", {method: "GET"})
		.then(function(response){
           	return response.json();
        })
        .then(function(data){
        	var something = data.arr.substring(1,data.arr.length - 1).split(", ");
        	console.log(something);
        	for (var i = something.length - 1; i >= 0; i--) {
        		var newItem = document.createElement("img");
				newItem.setAttribute("class", "responsive-img");
        		newItem.src = something[i];
        		document.getElementById("fileView").appendChild(newItem);
        		document.getElementById("fileView").appendChild(document.createElement("br"));
        		document.getElementById("fileView").appendChild(document.createElement("br"));
        	}
        })

}

document.getElementById("button4").addEventListener("click", upload);
document.getElementById("button5").addEventListener("click", optionDelete);
document.getElementById("button6").addEventListener("click", upChapter);











//return JSON.parse(JSON.stringify(response));
//return JSON.parse("{" + response);
//console.log(response.json());