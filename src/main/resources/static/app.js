var stompClient = null;
var channelMap = new Map();

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
    var urls = ["http://rss.cnn.com/rss/cnn_world.rss",
        "http://rss.cnn.com/rss/cnn_us.rss",
        "http://rss.cnn.com/rss/cnn_tech.rss",
        "http://rss.nytimes.com/services/xml/rss/nyt/World.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Africa.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Americas.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/AsiaPacific.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Europe.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/MiddleEast.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/US.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Education.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Politics.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Business.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Environment.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Economy.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/YourMoney.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Technology.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Sports.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/ProFootball.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Science.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Health.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Arts.xml",
        "http://rss.nytimes.com/services/xml/rss/nyt/Travel.xml",
        "http://feeds.bbci.co.uk/news/world/rss.xml",
        "http://feeds.bbci.co.uk/news/world/africa/rss.xml",
        "http://feeds.bbci.co.uk/news/world/asia/rss.xml",
        "http://feeds.bbci.co.uk/news/world/europe/rss.xml",
        "http://feeds.bbci.co.uk/news/world/latin_america/rss.xml",
        "http://feeds.bbci.co.uk/news/world/middle_east/rss.xml",
        "http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml",
        "http://feeds.bbci.co.uk/news/uk/rss.xml",
        "http://feeds.bbci.co.uk/news/england/rss.xml",
        "http://feeds.bbci.co.uk/news/northern_ireland/rss.xml",
        "http://feeds.bbci.co.uk/news/scotland/rss.xml",
        "http://feeds.bbci.co.uk/news/wales/rss.xml",
        "http://feeds.bbci.co.uk/news/business/rss.xml",
        "http://feeds.bbci.co.uk/news/politics/rss.xml",
        "http://feeds.bbci.co.uk/news/health/rss.xml",
        "http://feeds.bbci.co.uk/news/education/rss.xml",
        "http://feeds.bbci.co.uk/news/science_and_environment/rss.xml",
        "http://feeds.bbci.co.uk/news/technology/rss.xml",
        "http://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml",
        "https://www.theguardian.com/uk/rss",
        "https://www.theguardian.com/world/rss",
        "https://www.theguardian.com/uk/culture/rss",
        "https://www.theguardian.com/education/rss",
        "https://www.theguardian.com/science/rss",
        "https://99designs.com/tech-blog/feed.xml",
        "http://blogs.msdn.com/csharpfaq/rss.xml",
        "http://cordova.apache.org/rss.xml",
        "http://forums.sqlteam.com/c/high-availability.rss",
        "https://code.blender.org/rss",
        "http://blogs.msdn.com/clrteam/rss.xml",
        "http://tech.nitoyon.com/ja/blog/index.xml",
        "http://phonegap.com/rss.xml",
        "http://SeanKilleen.com/feed.xml",
        "http://engineering.curalate.com/feed.xml",
        "http://aakinshin.net/en/rss.xml",
        "http://tech.adroll.com/feed.xml",
        "http://www.codenameone.com/feed.xml"];
    urls.push($("#url1").val());
    stompClient.send("/app/hello", {}, JSON.stringify({'url': urls}));
}

function showGreeting(channelList) {
    channelList.forEach(function (channel, index) {
        channelMap.set(index, channel);
        var testdiv = document.createElement("div");
        testdiv.className = 'card';
        testdiv.setAttribute("index", index);
        testdiv.onclick = function () {
            $("#conversation").html("");
            var index = parseInt(this.getAttribute("index"));
            var channel = channelMap.get(index);
            showRSSFeeds(channel);
        };
        testdiv.innerHTML = channel.title;
        $("#RssIndex").append(testdiv);
    });
}

function showRSSFeeds(channel) {
    var channelIdentifier = channel.title;
    channelIdentifier = channelIdentifier.replace(/[^0-9A-Za-z]/gi, '');
    if ($("#cardView" + channelIdentifier).length <= 0) {
        createChannelCard(channel, channelIdentifier);
    }

    if ($("#listDiv" + channelIdentifier).length <= 0) {
        createItemListElement(channelIdentifier);
    }
    var length = channel.entries ? channel.entries.length : 0;
    for (var i = 0; i < length; i++) {
        var feed = channel.entries[i];
        if (true) {
            createItemCard(feed, channelIdentifier);
        }
    }
}

function createItemListElement(channelIdentifier) {
    var Listdiv = document.createElement('div');
    Listdiv.id = "listDiv" + channelIdentifier;
    $("#cardView" + channelIdentifier).append(Listdiv);
}

function createItemCard(feed, channelIdentifier) {
    var div = document.createElement('div');
    div.className = 'card';
    div.id = "item";
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
    $("#listDiv" + channelIdentifier).prepend(div);
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
    $("#conversation").append(carddiv);
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

