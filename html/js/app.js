var stompClient = null;

var socket = new SockJS('http://localhost:8080/quiz-websocket');
stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    console.log(frame);
    stompClient.subscribe('/all/messages', function(result) {
        show(JSON.parse(result.body));
    });

    stompClient.subscribe('/all/question', function(result) {
        showQuestion(JSON.parse(result.body));
        document.getElementById("correctOrWrong").innerHTML = ""
        document.getElementById("getQuestion").disabled = true
        document.getElementById("option1").disabled = false
        document.getElementById("option2").disabled = false
        document.getElementById("option3").disabled = false
        document.getElementById("option4").disabled = false
        document.getElementById("option1").classList.remove("d-none")
        document.getElementById("option2").classList.remove("d-none")
        document.getElementById("option3").classList.remove("d-none")
        document.getElementById("option4").classList.remove("d-none")
        document.getElementById("getQuestion").value = "Next question"
    });

    stompClient.subscribe('/user/specific', function(result) {
        console.log("Message received on /user/specific:", result);  // Log the complete message
        const messageBody = JSON.parse(result.body);
        console.log("Parsed Message:", messageBody); // Log the parsed content
        handleResult(messageBody);
    });

    stompClient.subscribe('/specific', function(result) {
        console.log("Message received on /user/specific:", result);  // Log the complete message
        const messageBody = JSON.parse(result.body);
        console.log("Parsed Message:", messageBody); // Log the parsed content
        handleResult(messageBody);
    });

    stompClient.subscribe('/all/readyForNextQuestion', function(result) {
        document.getElementById("getQuestion").disabled = false
    });

});

function getQuestion() {
    stompClient.send("/app/question", {});
}

function sendMessage() {
    var text = document.getElementById('text').value;
    stompClient.send("/app/application", {},
        JSON.stringify({'text':text}));
}

function submitOption(question, option) {
    stompClient.send("/app/submitOption", {},
        JSON.stringify({"question":question, "option": option}));
}

function submitOptionPrivate(question, option) {
    stompClient.send("/app/private", {},
        JSON.stringify({"question":question, "option": option, "username": sessionStorage.getItem("username")}));
}

function show(message) {
    var response = document.getElementById('messages');
    var p = document.createElement('p');
    p.innerHTML= "message: "  + message.text;
    response.appendChild(p);
}

function showQuestion(question) {
    var questionField = document.getElementById('question');
    var option1 = document.getElementById('option1');
    var option2 = document.getElementById('option2');
    var option3 = document.getElementById('option3');
    var option4 = document.getElementById('option4');
    questionField.innerHTML = question.text;
    option1.value = question.option1
    option2.value = question.option2;
    option3.value = question.option3;
    option4.value = question.option4;
}

function handleResult(result) {
    if (result === true) {
        let points = parseInt(document.getElementById("points").innerHTML)
        points++
        document.getElementById("points").innerHTML = points
        document.getElementById("correctOrWrong").innerHTML = "Correct"
        document.getElementById("correctOrWrong").classList.add("text-success")
        document.getElementById("correctOrWrong").classList.remove("text-danger")
        return
    }
    document.getElementById("correctOrWrong").innerHTML = "Wrong"
    document.getElementById("correctOrWrong").classList.add("text-danger")
    document.getElementById("correctOrWrong").classList.remove("text-success")
}

document.getElementById("getQuestion").addEventListener("click", (e)=> {
    getQuestion()
})

document.getElementById("option1").addEventListener("click", (e)=> {
    submitOptionPrivate(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("option2").addEventListener("click", (e)=> {
    submitOptionPrivate(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("option3").addEventListener("click", (e)=> {
    submitOptionPrivate(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("option4").addEventListener("click", (e)=> {
    submitOptionPrivate(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("submit").addEventListener("click", async (e) => {
    e.preventDefault();
    document.getElementById("register").classList.add("d-none");
    document.getElementById("registerComplete").classList.remove("d-none");
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch('http://localhost:8080/api/register', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username, password})
    });

    sessionStorage.setItem("username", username);
})
