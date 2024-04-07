var stompClient = null;

var socket = new SockJS('http://localhost:8080/quiz-websocket');
stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    stompClient.subscribe('/all/messages', function(result) {
        show(JSON.parse(result.body));
    });

    stompClient.subscribe('/all/question', function(result) {
        showQuestion(JSON.parse(result.body));
        document.getElementById("correctOrWrong").classList.remove("d-none");
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
        document.getElementById("scoreboardParent").classList.remove("d-none");
        document.getElementById("registerComplete").classList.add("d-none");
        document.getElementById("getQuestion").value = "Next question"
    });

    // stompClient.subscribe('/user/specific', function(result) {
    //     console.log("Message received on /user/specific:", result);  // Log the complete message
    //     const messageBody = JSON.parse(result.body);
    //     console.log("Parsed Message:", messageBody); // Log the parsed content
    //     showCorrectOrWrong(messageBody);
    // });

    stompClient.subscribe('/all/readyForNextQuestion', function(result) {
        document.getElementById("getQuestion").disabled = false
    });

    // update active players table
    stompClient.subscribe('/all/activePlayers', function(activePlayers) {
        activePlayers = JSON.parse(activePlayers.body);

        let activePlayersBody = document.createElement("tbody");
        activePlayersBody.id="activePlayersBody";
        activePlayers.forEach(activePlayer => {
            let tr = document.createElement("tr");
            let td = document.createElement("td");
            td.innerHTML = activePlayer;
            tr.appendChild(td);
            activePlayersBody.appendChild(tr);
        });
        document.getElementById("activePlayersBody").replaceWith(activePlayersBody)
    });

    stompClient.subscribe('/all/submitOption', function(result) {
        let response = JSON.parse(result.body);
        let username = sessionStorage.getItem("username");
        let scoreboard

        if (sessionStorage.getItem("scoreboard") === null || sessionStorage.getItem("scoreboard") === '') {
            scoreboard = []
        } else {
            scoreboard = JSON.parse(sessionStorage.getItem("scoreboard"));
        }

        let player = response;

        //create not existing user
        if (scoreboard.filter(scoreboardPlayer => scoreboardPlayer.username === player.username).length === 0) {
            scoreboard.push({"username": player.username, "points": 0})
        }

        //update scoreboard
        let updatedScoreboard = scoreboard.map((scoreBoardPlayer) => {
            if (scoreBoardPlayer.username === player.username && player.response === 'true') {
                scoreBoardPlayer.points++
            }
            return scoreBoardPlayer
        });

        if (sessionStorage.getItem("username") === player.username) {
            showCorrectOrWrong(player.response)
        }

        sessionStorage.setItem("scoreboard", JSON.stringify(updatedScoreboard))
        updateScoreboardHtml(updatedScoreboard, player)

        // let filteredPlayers = players.filter(player => player.name === username);
        //
        // if (typeof filteredPlayers !== 'undefined' && filteredPlayers.length > 0) {
        //     if (filteredPlayers.result) {
        //         let updatedScoreboard = scoreboard.map((scoreboardPlayer) =>  {
        //             if (scoreboardPlayer.username === username && scoreboardPlayer.response) {
        //                 scoreboardPlayer.points++
        //             }
        //             return scoreboardPlayer
        //         });
        //
        //         sessionStorage.setItem("scoreboard", updatedScoreboard)
        //         updateScoreboardHtml(updatedScoreboard)
        //         // let test = [
        //         //     {username: "umut", response: true},
        //         //     {username: "florian", response: false}
        //         // ]
        //     }
        // }
    });
});

function updateScoreboardHtml(updatedScoreboard, player) {
    let scoreBoard = document.getElementById("scoreboard");
    let trRows = scoreBoard.getElementsByTagName("tr");
    // let tdRows = Array.from(scoreBoard.getElementsByTagName("td"));
    // let tBody = scoreBoard.children[1]

    let users = Array.from(document.querySelectorAll("[data-name]")).map(user => user.innerHTML);
    // let points = Array.from(document.querySelectorAll("[data-points]")).map(point => point.innerHTML);

    // create not existing user in scoreboard
    updatedScoreboard.forEach(scoreBoardPlayer => {
        if (users.indexOf(scoreBoardPlayer.username) < 0) {
            let newUserTr = document.createElement("tr")
            let newUserNameTd = document.createElement("td")
            let newUserPointsTd = document.createElement("td")
            newUserNameTd.innerHTML = scoreBoardPlayer.username;
            newUserPointsTd.innerHTML = "";
            newUserNameTd.setAttribute("data-name", scoreBoardPlayer.username)
            newUserPointsTd.setAttribute("data-points", scoreBoardPlayer.username)
            newUserTr.append(newUserNameTd, newUserPointsTd);

            // new User added here
            document.getElementById("scoreboard").children[1].append(newUserTr);
        }

    });

    // update points of user
    console.log(player)
    let points = parseInt(updatedScoreboard.filter(scoreboardPlayer => scoreboardPlayer.username === player.username)[0].points);
    // let points = parseInt(document.querySelectorAll("td[data-points='" + player.username + "']")[0].innerHTML);
    document.querySelectorAll("td[data-points='" + player.username + "']")[0].innerHTML = points.toString()
}

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

function showCorrectOrWrong(result) {
    if (result === 'true') {
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
    submitOption(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("option2").addEventListener("click", (e)=> {
    submitOption(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("option3").addEventListener("click", (e)=> {
    submitOption(document.getElementById("question").innerHTML, e.target.value)
    document.getElementById("option1").disabled = true
    document.getElementById("option2").disabled = true
    document.getElementById("option3").disabled = true
    document.getElementById("option4").disabled = true
})

document.getElementById("option4").addEventListener("click", (e)=> {
    submitOption(document.getElementById("question").innerHTML, e.target.value)
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
    // const password = document.getElementById('password').value;

    const response = await fetch('http://localhost:8080/api/register', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username})
    });

    sessionStorage.setItem("username", username);

    document.getElementById("getQuestion").classList.remove("d-none");
    document.getElementById("activePlayersParent").classList.remove("d-none");
})
