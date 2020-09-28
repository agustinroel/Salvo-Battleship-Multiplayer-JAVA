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

const urlParams = new URLSearchParams(window.location.search);
const gp = urlParams.get('gp');

fetchJson('/api/game_view/' + gp, miInicializador).then(json => {
    app.gameView = json
    app.mainP()
    app.oponente()
    app.conversorDeLocations();
    //app.addingPaintByShipLocation(app.gameView.ships)
    app.paintingSalvoes(app.gameView.playerSalvoes)
    app.hits(app.gameView.playerSalvoes, app.gameView.ships)
}).catch(function (error) {
    console.log("Error :c", error)
});




var app = new Vue({
    el: "#app",
    data: {
        gameView: {},
        gameRows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameColumns: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        mainPlayer: {},
        opponent: {},
        grid: null,
        location: [],
        shipsLocations: [],
        //CONFIGURACION DEL TABLERO E INICIALIZADOR 
        options: {
            //grilla de 10 x 10
            column: 10,
            row: 10,
            //separacion entre elementos (les llaman widgets)
            verticalMargin: 0,
            //altura de las celdas
            disableOneColumnMode: true,
            //altura de las filas/celdas
            cellHeight: 40,
            //necesario
            float: true,
            //desabilitando el resize de los widgets
            disableResize: true,
            //false permite mover los widgets, true impide
            staticGrid: false
        },
        newSalvos: []
    },
    methods: {
        mainP: function () {
            app.mainPlayer = app.gameView.gamePlayers.filter(x => x.gpid === app.gameView.id)[0];
        },
        oponente: function () {
            if (app.gameView.gamePlayers.length < 2) {
                app.opponent == "AGUARDANDO OPONENETE"
            } else {
                app.opponent = app.gameView.gamePlayers.filter(x => x.gpid !== app.gameView.id)[0];
            }
        },
        paint: function (a, b) {
            let td = document.getElementById(a)
            td.classList.add(b);
            td.style.cursor = "not-allowed";
        },

        /*addingPaintByShipLocation: function (arrayDeShips) {
            arrayDeShips.forEach(ship => {
                ship.location.forEach(l => {
                    this.paint(l, "pintado")
                })
            })
        },*/
        paintSalvos: function (a, b, c, d) {
            let td = document.getElementById(a)
            td.classList.add(b);
            td.innerHTML = c;
            td.style.backgroundColor = d;
            td.style.cursor = "not-allowed";
        },
        paintingSalvoes: function (arrayDeSalvos) {
            arrayDeSalvos.forEach(playerSalvo => {
                if (playerSalvo.id === app.mainPlayer.id)
                    playerSalvo.salvoes.forEach(salvo => {
                        var color = '#' + (0x1000000 + (Math.random()) * 0xffffff).toString(16).substr(1, 6);
                        salvo.locations.forEach(l => this.paintSalvos('salvo' + l, "shoot", salvo.turn, color))
                    })
            })
        },
        hits: function (arrayDeSalvos, arrayDeShips) {

            arrayDeSalvos.forEach(playerSalvo => {
                if (playerSalvo.id === app.opponent.id) { //nos referimos a los salvos del oponente
                    playerSalvo.salvoes.forEach(salvo => { //recorro los salvos del oponente 
                        salvo.locations.forEach(sl => { //para cada location en cada salvo
                            arrayDeShips.forEach(ship => { //recorro el listado de naves
                                if (ship.location.includes(sl)) { //pregunto si el listado de naves incluye la location del salvo
                                    this.paint(sl, "bg-danger") //si es true, se pinta de rojo el td que coincide
                                }
                            })
                        })
                    })
                }
            })
        },
        logout: function () {
            $.post("/api/logout").done(function () {
                $(".login").fadeIn(), $(".logout").addClass("invisible"), console.log("logged out");
                window.location.replace("/web/games.html");
            })
        },
        backHome: function () {
            window.location.href = "/web/games.html"
        },
        postNewShips: function () {
            const ships = document.querySelectorAll("#submarine,#carrier,#patrol,#destroyer,#battleship");
            ships.forEach(ship => {
                //obteniendo valores del widget
                let itemContent = ship.parentElement;
                let itemX = parseInt(itemContent.dataset.gsX);
                let itemY = parseInt(itemContent.dataset.gsY);
                let itemWidth = parseInt(itemContent.dataset.gsWidth);
                let itemHeight = parseInt(itemContent.dataset.gsHeight);
                if (itemWidth > itemHeight) {
                    app.location = [];
                    let n = 1;
                    while (n <= itemWidth) {
                        app.location.push(String.fromCharCode(itemY + 65) + (itemX + n));
                        n++;
                    }
                    var newShip = {
                        shipType: ship.id,
                        locations: this.location,
                    };
                } else {
                    app.location = [];
                    let n = 0;
                    while (n < itemHeight) {
                        app.location.push(
                            String.fromCharCode(itemY + 65 + n) + (itemX + 1)
                        );
                        n++;
                    }
                    var newShip = {
                        shipType: ship.id,
                        locations: this.location,
                    };
                }
                this.shipsLocations.push(newShip);
            })
            console.log(JSON.stringify(this.shipsLocations))
            $.post({
                url: "/api/games/players/" + gp + "/ships",
                data: JSON.stringify(app.shipsLocations),
                dataType: "text",
                contentType: "application/json",
            }).then(function () {
                app.grid.enableMove(false)
                const ships = document.querySelectorAll("#submarine,#carrier,#patrol,#destroyer,#battleship")
                ships.forEach(ship => {
                    ship.parentElement.onclick = null;
                })
                $(".saveShips").fadeOut();
            })
        },
        conversorDeLocations: function () {
            //iniciando la grilla en modo libe statidGridFalse
            app.grid = GridStack.init(app.options, '#grid');

            if (app.gameView.ships.length > 0) {
                app.gameView.ships.forEach(ship => {
                    //buscar primera y segunda locacion solamente
                    var l = ship.location[0]
                    var y = l.substring(0, 1);
                    var x = l.substring(1);
                    var firstLocationEjeX = parseInt(x) - 1
                    var firstLocationEjeY = y.charCodeAt() - 65;


                    l = ship.location[1]
                    var z = l.substring(0, 1);
                    var j = l.substring(1);
                    var secondLocationEjeX = parseInt(j) - 1;
                    var secondLocationEjeY = z.charCodeAt() - 65;

                    //agregando elementos (widget) desde el javascript
                    //elemento,x(horizontal),y(vertical),width,height
                    app.grid.enableMove(false)
                    if (ship.type == "patrol-boat") {
                        if (firstLocationEjeY == secondLocationEjeY) {
                            this.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolHorizontal"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 2, 1);
                        } else {
                            this.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolVertical"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 1, 2);
                        }
                    }
                    if (ship.type == "submarine") {
                        if (firstLocationEjeY == secondLocationEjeY) {
                            this.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineHorizontal"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 3, 1);
                        } else {
                            this.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineVertical"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 1, 3);
                        }
                    }
                    if (ship.type == "carrier") {
                        if (firstLocationEjeY == secondLocationEjeY) {
                            this.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierHorizontal"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 4, 1);
                        } else {
                            this.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierVertical"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 1, 4);
                        }
                    }
                    if (ship.type == "destroyer") {
                        if (firstLocationEjeY == secondLocationEjeY) {
                            this.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerHorizontal"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 3, 1);
                        } else {
                            this.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerVertical"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 1, 3);
                        }
                    }
                    if (ship.type == "battleship") {
                        if (firstLocationEjeY == secondLocationEjeY) {
                            this.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipHorizontal"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 5, 1);
                        } else {
                            this.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipVertical"></div></div>',
                                firstLocationEjeX, firstLocationEjeY, 1, 5);
                        }
                    }
                })
            } else {
                app.newGameShips();
                app.rotarBarcos();

            };
        },
        rotarBarcos: function () {
            //rotacion de las naves
            //obteniendo los ships agregados en la grilla
            const ships = document.querySelectorAll("#submarine,#carrier,#patrol,#destroyer,#battleship");
            ships.forEach(ship => {
                //asignando el evento de click a cada nave
                ship.parentElement.onclick = function (event) {
                    //obteniendo el ship (widget) al que se le hace click
                    let itemContent = event.target;
                    //obteniendo valores del widget
                    let itemX = parseInt(itemContent.parentElement.dataset.gsX);
                    let itemY = parseInt(itemContent.parentElement.dataset.gsY);
                    let itemWidth = parseInt(itemContent.parentElement.dataset.gsWidth);
                    let itemHeight = parseInt(itemContent.parentElement.dataset.gsHeight);

                    //si esta horizontal se rota a vertical sino a horizontal
                    if (itemContent.classList.contains(itemContent.id + 'Horizontal')) {
                        //veiricando que existe espacio disponible para la rotacion
                        if (app.grid.isAreaEmpty(itemX, itemY + 1, itemHeight, itemWidth - 1) && (itemY + (itemWidth - 1) <= 9)) {
                            //la rotacion del widget es simplemente intercambiar el alto y ancho del widget, ademas se cambia la clase
                            app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                            itemContent.classList.remove(itemContent.id + 'Horizontal');
                            itemContent.classList.add(itemContent.id + 'Vertical');
                        } else {
                            alert("Espacio no disponible");
                        }
                    } else {
                        if (app.grid.isAreaEmpty(itemX + 1, itemY, itemHeight - 1, itemWidth) && (itemX + (itemHeight - 1) <= 9)) {
                            app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                            itemContent.classList.remove(itemContent.id + 'Vertical');
                            itemContent.classList.add(itemContent.id + 'Horizontal');
                        } else {
                            alert("Espacio no disponible");
                        }
                    }
                }
            })
        },
        newGameShips: function () {

            this.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineHorizontal"></div><div/>',
                1, 1, 3, 1);

            this.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierVertical"></div><div/>',
                9, 1, 1, 4);

            this.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolHorizontal"></div><div/>',
                2, 4, 2, 1);

            this.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerVertical"></div><div/>',
                6, 4, 1, 3);

            this.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipHorizontal"></div><div/>',
                2, 8, 5, 1);
        },
        seleccionarSalvos: function (x) {
            if (app.grid.enableMove(false)) {
                this.paint(x, "selected")
                app.newSalvos.push(x)
                //falta deseleccion
                console.log(app.newSalvos);
            }
        },
        postNewSalvo: function () {
            if (app.newSalvos.length != 5) {
                alert("You must select 5 locations to fire")
            } else {
                if (app.gameView.playerSalvoes[0].lenght == 0) {
                    let turnNumber = 1
                }
                var salvo =  {
                    turnNumber: 1,
                    locations: app.newSalvos
                }
                console.log(JSON.stringify(salvo));
                $.post({
                    url: "/api/games/players/" + gp + "/salvos",
                    data: JSON.stringify(salvo),
                    dataType: "text",
                    contentType: "application/json",
                }).done(function () {
                  console.log(JSON.stringify(salvo))
                })
            }
        }
    }
})