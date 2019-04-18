var stompClient = null;
var channelMap = new Map();
var startTime = null;
var endTime = null;
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
    "http://www.codenameone.com/feed.xml",
    "http://www.espn.com/espn/rss/news",
    "http://rss.nytimes.com/services/xml/rss/nyt/US.xml",
    "https://feeds.megaphone.fm/LM1344278906",
    "https://www.npr.org/rss/podcast.php?id=510298",
    "https://business.financialpost.com/feed/",
    "https://hacked.com/feed",
    "https://www.techrepublic.com/rssfeeds/articles/",
    "https://themerkle.com/feed/",
    "https://www.naic.org/press.xml",
    "https://rss.dailyfx.com/feeds/all",
    "https://www.which.co.uk/news/feed/",
    "https://www.technologyreview.com/stories.rss",
    "https://99bitcoins.com/feed/",
    "https://www.inverse.com/feed/articles/1.rss",
    "https://wccftech.com/feed/",
    "https://boingboing.net/feed",
    "https://www.prnewswire.com/rss/all-news-releases-from-PR-newswire-news.rss",
    "https://www.naic.org/newswire.xml",
    "https://www.ccn.com/feed/",
    "http://rss.nzherald.co.nz/rss/xml/nzhtsrsscid_000000698.xml",
    "https://cointelegraph.com/feed",
    "https://www.vanityfair.com/feed/rss",
    "https://news.bitcoin.com/feed/",
    "https://cms.qz.com/feed/",
    "https://www.androidauthority.com/feed/",
    "http://fortune.com/feed/",
    "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGx1YlY4U0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en",
    "https://www.nasa.gov/rss/dyn/educationnews.rss",
    "https://www.nasa.gov/rss/dyn/breaking_news.rss",
    "https://blogs.nasa.gov/stationreport/feed/",
    "https://www.nasa.gov/rss/dyn/mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss",
    "https://www.nasa.gov/rss/dyn/chandra_images.rss",
    "http://www1.cbn.com/app_feeds/rss/news/rss.php?section=world",
    "https://rss.weatherzone.com.au/?u=12994-1285&lt=aploc&lc=12495&obs=1&fc=1&warn=1",
    "https://www.npr.org/rss/podcast.php?id=510298",
    "https://business.financialpost.com/feed/",
    "https://hacked.com/feed/",
    "https://www.techrepublic.com/rssfeeds/articles/",
    "https://themerkle.com/feed/",
    "https://www.naic.org/press.xml",
    "https://rss.dailyfx.com/feeds/all",
    "https://www.which.co.uk/news/feed/",
    "https://www.technologyreview.com/stories.rss",
    "https://99bitcoins.com/feed/",
    "https://www.inverse.com/feed/articles/1.rss",
    "https://wccftech.com/feed/",
    "https://boingboing.net/feed",
    "https://www.prnewswire.com/rss/all-news-releases-from-PR-newswire-news.rss",
    "https://www.naic.org/newswire.xml",
    "https://www.ccn.com/feed/",
    "http://rss.nzherald.co.nz/rss/xml/nzhtsrsscid_000000698.xml",
    "https://cointelegraph.com/feed/",
    "https://www.vanityfair.com/feed/rss",
    "https://news.bitcoin.com/feed/",
    "https://cms.qz.com/feed/",
    "https://www.androidauthority.com/feed/",
    "http://fortune.com/feed/",
    "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGx1YlY4U0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en",
    "https://www.nasa.gov/rss/dyn/educationnews.rss",
    "https://www.nasa.gov/rss/dyn/breaking_news.rss",
    "https://blogs.nasa.gov/stationreport/feed/",
    "https://www.nasa.gov/rss/dyn/mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss", "http://d.hatena.ne.jp/Arufa/rss", "http://blog.livedoor.jp/dqnplus/index.rdf", "http://shokai.org/blog/feed", "http://qiita.com/tags/Python/feed.atom", "http://mizchi.hatenablog.com/rss", "https://github.com/blog/engineering.atom", "http://wazanova.jp/rss", "http://ssig33.com/feed", "http://faithandbrave.hateblo.jp/rss", "http://rss.rssad.jp/rss/gihyo/dev/feed/rss1", "http://vim-jp.org/rss.xml", "http://blog.ku-suke.jp/feed", "http://jkondo.hatenablog.com/feed", "http://petitviolet.hatenablog.com/feed", "http://www.infoq.com/jp/feed/news?token=e8FDd2q3qKWUO1LSHM5335FTusRLcn9f", "http://slashdot.jp/slashdotjp.rss", "http://mirakui.hatenablog.com/feed", "http://googledevjp.blogspot.com/feeds/posts/default", "http://nerds.airbnb.com/feed/", "http://d.hatena.ne.jp/t2y-1979/rss", "http://r7kamura.hatenablog.com/feed", "http://d.hatena.ne.jp/thinca/rss", "http://blog.ishkawa.org/atom.xml", "http://d.hatena.ne.jp/iwiwi/rss", "http://old.imoz.jp/feed/", "http://googleforstudents.blogspot.com/feeds/posts/default", "http://feeds.feedburner.com/Hirokijp", "http://jp.techcrunch.com/feed/", "http://blog.katty.in/feed", "http://blog.tokumaru.org/feeds/posts/default", "http://blog.jenniferdewalt.com/rss", "http://d.hatena.ne.jp/hyuki/rss", "http://devtoolstips.com/rss", "http://d.hatena.ne.jp/shu223/rss", "http://research.preferred.jp/feed/", "http://d.hatena.ne.jp/t2y-1979+1979/rss", "http://blog.zoncoen.net/atom", "http://heartbeats.jp/hbblog/atom.xml", "http://tech.nitoyon.com/ja/blog/index.xml", "http://yusuke.be/rss", "http://googlejapan.blogspot.com/atom.xml", "http://j.ktamura.com/feed.rss", "http://d.hatena.ne.jp/r_kurain/rss", "http://mattn.kaoriya.net/index.rss", "http://feeds.bulknews.net/bulknews", "http://feeds.feedburner.com/make_jp", "http://hmsk.hatenablog.com/feed", "http://www.strnet.com/feed", "http://qiita.com/tags/go/feed.atom", "https://news.ycombinator.com/rss", "http://thechangelog.com/tagged/go/feed/", "http://itpro.nikkeibp.co.jp/rss/develop.rdf", "http://brbranch.jp/blog/feed", "http://fullrss.net/a/http/k.hatena.ne.jp/keywordblog/Python?mode=rss", "http://june29.jp/feed/", "http://d.hatena.ne.jp/shinichiro_h/rss", "http://frsyuki.hatenablog.com/rss", "http://engineer.dena.jp/atom.xml", "http://blog.ruedap.com/feed", "http://deeeet.com/writing/atom", "http://t18mkx.lolipop.jp/harajuku_aa/?feed=rss2", "http://blog.kentarok.org/feed", "http://1000ch.net/rss", "http://tsuchinoko.dmmlabs.com/?feed=rss2", "http://blogger.ukai.org/feeds/posts/default", "http://blog.niw.at/rss", "http://b.hatena.ne.jp/search/tag?q=Objective-C&mode=rss", "http://blog.codinghorror.com/rss", "https://tech.dropbox.com/feed/", "http://d.hatena.ne.jp/Jxck/rss", "http://design.kayac.com/rss.xml", "http://itpro.nikkeibp.co.jp/rss/ITpro.rdf", "http://kyokomi.hatenablog.com/feed", "http://yudoufu.hatenablog.jp/feed", "http://tech.camph.net/?feed=rss2", "http://did2memo.net/feed/", "http://d.hatena.ne.jp/CortYuming/rss", "http://tech.kayac.com/atom.xml", "http://neovim.org/news.xml", "http://mikirepo.blogspot.com/feeds/posts/default", "http://cocoabu.com/feed", "http://d.hatena.ne.jp/hiratara/rss", "http://plus.vc/feed/", "http://dev.classmethod.jp/feed/", "http://blog.katsuma.tv/atom.xml", "http://feeds.rebuild.fm/rebuildfm", "http://blog.kenjiskywalker.org/atom.xml", "http://methane.hatenablog.jp/feed", "http://techlife.cookpad.com/feed", "http://blog.livedoor.jp/dankogai/index.rdf", "http://kazukichi0914.hatenablog.com/feed", "http://veadardiary.blog29.fc2.com/?xml", "http://d.hatena.ne.jp/rx7/rss", "http://qiita.com/tags/cocos2d-x/feed", "http://blog.kushii.net/index.rdf", "http://cpp.aquariuscode.com/feed", "http://feeds.feedburner.com/TheSartorialist", "http://d.hatena.ne.jp/next49/rss", "http://rssblog.ameba.jp/mizunokeiya/rss20.xml", "http://satoshi.blogs.com/life/atom.xml", "http://blog.livedoor.jp/keumaya-china/index.rdf", "http://t01545mh.hatenablog.com/feed", "http://d.hatena.ne.jp/shi3z/rss", "http://www.muji.net/mt/ie/mitakanoie/atom.xml", "http://d.hatena.ne.jp/mamoruk/rss", "http://wktokyo.jp/blog/feed/", "http://hi240.blogspot.com/feeds/posts/default", "http://www.imc.cce.i.kyoto-u.ac.jp/ja/feed/", "http://michikakeworldlog.blogspot.com/feeds/posts/default", "http://rssblog.ameba.jp/shibuya/rss20.xml", "http://hirokitakaba.github.io/atom.xml", "http://push--up.com/feed", "http://netgeek.biz/feed", "https://hiroakis.com/blog/feed/", "http://masaki0720.tumblr.com/rss", "http://rssblog.ameba.jp/masaki32000/rss20.xml", "http://b.hatena.ne.jp/articles.rss", "http://kai-you.net/contents/feed.rss", "http://feed.rssad.jp/rss/engadget/rss", "http://fullrss.net/a/http/feed.rssad.jp/rss/gigazine/rss_2.0", "http://www.tsukaueigo.com/index.rdf", "http://feeds.feedburner.com/DavidCramernet", "http://feeds.feedburner.com/DougHellmann", "http://page2rss.com/rss/74c16aad768113fe7bdfe5f575f4b12b", "http://postd.cc/feed/", "http://culturedcode.com/things/blog/feed", "http://feeds.feedburner.com/holman", "http://blog.codeschool.com/rss", "http://ushijima1129.blog117.fc2.com/?xml", "http://blog.livedoor.jp/hitoshione/index.rdf", "http://www.hyuki.com/d/rss.xml", "http://www.shuzo.co.jp/blog/atom.xml", "http://rssblog.ameba.jp/itsuyaruka/rss20.xml", "http://www.ideaxidea.com/feed", "http://feeds.gawker.com/lifehacker/full", "http://www.100shiki.com/feed", "http://fullrss.net/a/http/feeds.lifehacker.jp/rss/lifehacker/index.xml", "http://b.hatena.ne.jp/location/tokyo/rss"];

function setTimer(connected) {
    if (connected) {
        $("#executionTime").hide();
        startTime = new Date().getTime();
        $("#fetchStatus").text("Status: In Progress");
    }
    else {
        $("#fetchStatus").text("Status: Complete :: ");
        $("#executionTime").show();
        endTime = new Date().getTime();
        var t = endTime - startTime;
        $("#executionTime").text("Total time: " + t);
    }
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/rss/feeds', function (greeting) {
            setTimer(false);
            showGreeting(JSON.parse(greeting.body).channel);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendName() {
    var uniqueUrls = [];
    $.each(urls, function (i, el) {
        if ($.inArray(el, uniqueUrls) === -1) uniqueUrls.push(el);
    });
    var runType = $("#runType").val();
    var runSize = $("#runSize").val() == "all" ? uniqueUrls : uniqueUrls.splice(0, parseInt($("#runSize").val()));
    setTimer(true);
    $("#fetchStatus").show();
    stompClient.send("/app/fetchFeed", {}, JSON.stringify({'url': runSize, 'mode': runType}));
    $("#RssFeed").html("");
    $("#RssIndex").html("");
}

function addUrl() {
    if ($("#url").val() !== "")
        urls.push($("#url").val());
}
function showGreeting(channelList) {
    channelList.forEach(function (channel, index) {
        if (channel != null)
            channelMap.set(index, channel);
        var testdiv = document.createElement("div");
        testdiv.className = 'card';
        testdiv.setAttribute("index", index);
        testdiv.onclick = function () {
            $("#RssFeed").html("");
            var index = parseInt(this.getAttribute("index"));
            var channel = channelMap.get(index);
            if (channel != null)
                showRSSFeeds(channel);
        };
        testdiv.innerHTML = channel ? channel.title : "Channel " + index;
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
    $("#RssFeed").prepend(carddiv);
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendName(); });
    $("#addUrl").click(function () {
        addUrl();
    });
});

