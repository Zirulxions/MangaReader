function $(id){
		return document.getElementById(id);
	}

function createAccount(){
	var data = {
			password: $('password').value,
			username: $('username').value,
			name: $('name').value,
			email: $('email').value,
    };          
	console.log(data)
	let config = {
		method: 'POST',
		body: JSON.stringify(data),
        };
	fetch("./Register", config)
		.then(function(response){
			return response.json();
		})
		.then(function(data){
			console.log(data);
			alert(data.message);
			if(data.redirect != null && data.redirect != undefined){
            	window.location.href = data.redirect;
         	}
		});
	}

function validateEmail(email) {
	var re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
	return re.test(email);
}

function validate() {
    var email = document.getElementById("email").value;
    var username = document.getElementById("username").value;
    var name = document.getElementById("name").value;
    var password = document.getElementById("password").value;
    if(email.trim() == "" || username.trim() == "" || name.trim() == "" || password.trim() == ""){
        document.getElementById("button3").disabled = true;
    } else {
        if (validateEmail(email)) {
            document.getElementById("button3").disabled = false;
        } else {
            document.getElementById("button3").disabled = true;
        }
        return false;
    }
}

document.getElementById("button3").addEventListener("click", createAccount);

function SwitchURL(){
	history.replaceState({},"Register", "Re.k8j2vdk.g.jkzvz84kl1.is.af7223.t.kjkkabofajs.er.Reggakbster.html");
	document.getElementById("button3").disabled = true;
}

function forceLower(strInput) {
	strInput.value=strInput.value.toLowerCase();
}