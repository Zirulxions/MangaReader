function $(id) {
	return document.getElementById(id);
}

function logIn(){
       	let body = {
       		username: document.getElementById("username").value,
      		password: document.getElementById("password").value
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

$("Start").addEventListener("click", logIn);

function SwitchURL(){
	history.replaceState({},"Login", "L.hd92asjja.o.a2j11.g.12DA234w.i.efsddaSDAGA.n.andreita.html");
}