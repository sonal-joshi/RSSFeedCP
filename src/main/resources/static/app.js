var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).channel);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    var urls = [];
    urls.push($("#url1").val());
    urls.push($("#url2").val());
    urls.push($("#url3").val());
    stompClient.send("/app/hello", {}, JSON.stringify({'url': urls}));
}

function showGreeting(channelList) {
    channelList.forEach(function (channel, index) {
        var channelIdentifier = channel.title;
        channelIdentifier = channelIdentifier.replace(/[^0-9A-Za-z]/gi, '');
        if ($("#cardView" + channelIdentifier).length <= 0) {
            $("#conversation").append(createChannelCard(channel, channelIdentifier));
        }
        if ($("#listDiv" + channelIdentifier).length <= 0) {
            var Listdiv = document.createElement('div');
            Listdiv.id = "listDiv" + channelIdentifier;
            var identifier = "#cardView" + channelIdentifier;
            $(identifier).append(Listdiv);
        }
        var length = channel.entries ? channel.entries.length : 0;
        for (var i = 0; i < length; i++) {
            var feed = channel.entries[i];
            if ($("#listDiv" + channelIdentifier + " > " + "#item" + i).length <= 0) {
                var div = document.createElement('div');
                div.className = 'card';
                div.id = "item" + i;
                var innderdiv = document.createElement('div');
                innderdiv.className = 'card-container';
                innderdiv.innerHTML = "<h4><b>" + feed.title + "</b></h4>";
                var e = document.createElement('div');
                e.className = 'card-description'
                e.innerHTML = feed.description;
                for (var j = 0; j < e.childNodes.length; j++) {
                    innderdiv.appendChild(e.childNodes[0]);
                }
                if (feed.guid) {
                    if ("true" == feed.guid.isPermLink) {
                        var anchorElement = document.createElement('a');
                        anchorElement.className = 'link';
                        anchorElement.href = feed.guid.value;
                        anchorElement.innerHTML = "<u>Read Full Article</u>";
                        innderdiv.appendChild(anchorElement);
                    }
                }
                div.appendChild(innderdiv);
                $("#listDiv" + channel.title).prepend(div);
            }
        }
    });
}

function createChannelCard(channel, channelIdentifier) {
    var carddiv = document.createElement('div');
    carddiv.className = 'card';
    carddiv.id = "cardView" + channelIdentifier;
    carddiv.innerHTML = "<h4><b>" + channel.title + "</b></h4>" + "<p>" + channel.description + "<br> Last Build Data: " + channel.lastBuildDate + "<br> Published Date: " + channel.pubDate + "</p>";
    if (channel.image) {
        var image = document.createElement('img');
        image.className = 'image';
        image.src = channel.image ? channel.image.url : null;
        image.alt = channel.image ? channel.image.title : null;
        if (channel.image && channel.image.link)
            image.onclick = function (channel) {
                window.location(channel.image.link)
            }
        carddiv.appendChild(image);
    }
    return carddiv;
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

