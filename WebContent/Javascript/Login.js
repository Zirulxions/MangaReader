function $(id) {
	return document.getElementById(id);
}

function logIn(){
       	let body = {
           	username: $("username").value,
      		password: $("password").value
       	};
      	console.log(body)
       	fetch("./Login", {method: "POST", body:JSON.stringify(body)})
           	.then(function(resp){
               	return resp.json();
           	})
           	.then(function(data){
               	console.log(data);
               	if(data.redirect != null && data.redirect != undefined){
               		sessionStorage.setItem("name",body.username);
                	window.location.href = data.redirect;
             	}
           	});
		}

$("button1").addEventListener("click", logIn);