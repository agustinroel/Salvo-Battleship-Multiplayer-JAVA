var miInicializador = {
     method: 'GET',
     headers: {},
     mode: 'cors',
     cache: 'default'
};

function fetchJson(url, init) {
     return fetch(url, init).then(function (response) {
          if (response.ok) {
               return response.json();
          }
          throw new Error(response.statusText);
     });
}

fetchJson('/api/games', miInicializador).then(json => {
     app.games = json.games
     app.player = json.player
     console.log(app.games)
     app.getLeaderboard()
}).catch(function (error) {
     console.log("Error :c", error)
});


var app = new Vue({
     el: "#app",
     data: {
          games: [],
          player: null,
          leaderboard: [],
          maxScore: 0,
          email: "",
          password: "",
          mainPlayer: null,
          mainPlayerGpId: 0,
     },
     methods: {
          getLeaderboard: function () {
               app.games.forEach(game => {
                    game.gamePlayers.forEach(gp => {
                         let x = app.leaderboard.findIndex(player => player.id === gp.id)
                         if (x == -1) {

                              player = {
                                   gpid: gp.gpid,
                                   id: gp.id,
                                   email: gp.email,
                                   wins: 0,
                                   losses: 0,
                                   ties: 0,
                                   totalScore: 0
                              }

                              if (gp.score != null) {
                                   if (gp.score == 1) {
                                        player.wins += 1;
                                        player.totalScore += 1;
                                   } else if (gp.score == 0.5) {
                                        player.ties += 1;
                                        player.totalScore += 0.5;
                                   } else if (gp.score == 0.0) {
                                        player.losses += 1;
                                   }
                              }
                              if (player.totalScore > app.maxScore) {
                                   app.maxScore = player.totalScore
                              }
                              app.leaderboard.push(player);
                         } else {

                              if (gp.score != null) {
                                   if (gp.score == 1) {
                                        app.leaderboard[x].wins += 1;
                                        app.leaderboard[x].totalScore += 1;
                                   } else if (gp.score == 0.5) {
                                        app.leaderboard[x].ties += 1;
                                        app.leaderboard[x].totalScore += 0.5;
                                   } else if (gp.score == 0.0) {
                                        app.leaderboard[x].losses += 1;
                                   }
                              }
                              if (app.leaderboard[x].totalScore > app.maxScore) {
                                   app.maxScore = app.leaderboard[x].totalScore
                              }
                         }
                    })
               })
          },
          login: function () {
               $.post("/api/login", {
                    email: app.email,
                    pwd: app.password
               }).done(function () {
                    $.get("/api/games").then(json => {
                         app.games = json.games
                         app.player = json.player
                         console.log("Logged in!");           
                         })
                    })
               },

          signup: function () {
               $.post("/api/players", {
                    email: app.email,
                    password: app.password
               }).done(function () {
                   alert("Signed Up Succesfully!");
               })
          },

          logout: function () {
               $.post("/api/logout").done(function () {
                    console.log("logged out");
                    location.href = "games.html";
               })
          },

          createGame: function () {
               $.post("/api/games").done(function (games) {
                    location.href = "game.html?gp=" + games.gpid;
               })
          },

          joinGame: function (id) {
               $.post("/api/game/" + id + "/players").done(function (games) {
                    location.href = "game.html?gp=" + games.gpid;
               })
          }
     }
})

/*function stayLoggedIn() {
     if (sessionStorage.getItem("user") != null) {
          app.email = sessionStorage.getItem("user")
          app.password = sessionStorage.getItem("pass")
          app.login()
     }
}
stayLoggedIn();*/