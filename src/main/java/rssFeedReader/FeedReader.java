package rssFeedReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class FeedReader implements Runnable {

    static final String TITLE = "title";
    static final String IS_PERM_LINK = "isPermaLink";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String IMAGE = "image";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String LAST_BUILD_DATE = "lastBuildDate";
    static final String GUID = "guid";
    static final String IMAGEURL = "url";
    private static ArrayList<Channel> output = new ArrayList<Channel>();
    ArrayList<URL> url;

    public FeedReader(ArrayList<String> url) {
        // TODO Auto-generated constructor stub
        try {
            this.url = new ArrayList<URL>();
            for (String cur : url)
                this.url.add(new URL(cur));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public FeedReader() {

    }

    public synchronized void clearOutputList(){
        this.output.clear();
    }

    public Channel fetchFeed(URL url) {
        String title = null, link = null, description = null, language = null, copyright = null, author = null, imageUrl = null, pubdate = null, lastBuildDate = null;
        Guid guid = null;
        Image image = null;
        boolean isFeedHeader = true;
        Channel channel = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = read(url);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName().getLocalPart();
                    switch (localPart) {

                        case ITEM:
                            if (isFeedHeader) {
                                isFeedHeader = false;
                                channel = new Channel(title, description, language, copyright, lastBuildDate, pubdate, image);
                            }
                            title = "";
                            description = "";
                            language = "";
                            lastBuildDate = "";
                            copyright = "";
                            guid = null;
                            break;
                        case LAST_BUILD_DATE:
                            lastBuildDate = getCharacterData(event, eventReader);
                            break;
                        case TITLE:
                            title = getCharacterData(event, eventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(event, eventReader);
                            break;
                        case LINK:
                            link = getCharacterData(event, eventReader);
                            break;
                        case GUID:
                            guid = getGuid(event, eventReader);
                            break;
                        case LANGUAGE:
                            language = getCharacterData(event, eventReader);
                            break;
                        case AUTHOR:
                            author = getCharacterData(event, eventReader);
                            break;
                        case PUB_DATE:
                            pubdate = getCharacterData(event, eventReader);
                            break;
                        case COPYRIGHT:
                            copyright = getCharacterData(event, eventReader);
                            break;

                        case IMAGEURL:
                            imageUrl = getCharacterData(event, eventReader);
                            break;
                    }

                } else if (event.isEndElement()) {
                    switch (event.asEndElement().getName().getLocalPart()) {
                        case ITEM:
                            Feed feed = new Feed();
                            feed.setAuthor(author);
                            feed.setDescription(description);
                            feed.setGuid(guid);
                            feed.setLink(link);
                            feed.setTitle(title);
                            feed.setPubDate(pubdate);
                            channel.getEntries().add(feed);
                            break;
                        case IMAGE:
                            image = new Image(title, imageUrl, link);
                            break;
                    }
                    continue;
                }

            }
        } catch (XMLStreamException e) {
//            System.out.println("RSS feed could not be parsed due to some illegal format");
            return null;
        }
        if (channel != null)
            setlist(channel);
        return channel;
    }

    private InputStream read(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            System.out.println("URL not valid: " + url.toString());
            return null;
        }
    }

    private synchronized String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        StringBuilder sw = null;
        XMLOutputFactory of = XMLOutputFactory.newInstance();
        XMLInputFactory f = XMLInputFactory.newInstance();
        String result = "";
        if (event.asStartElement().getName().getLocalPart().equals(DESCRIPTION)) {
            while (eventReader.hasNext()) {
                if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(DESCRIPTION)) {
                    sw = new StringBuilder();
                } else if (event.isEndElement()
                        && event.asEndElement().getName().getLocalPart().equals(DESCRIPTION)) {
                    break;
                } else if (sw != null) {
                    if (event instanceof Characters) {
                        sw.append(event.asCharacters().getData());
                    }
                }
                event = eventReader.nextEvent();
            }
            result = sw.toString();
        } else {
            event = eventReader.nextEvent();
            if (event instanceof Characters) {
                result = event.asCharacters().getData();
            }
        }

        return result;
    }

    public synchronized Guid getGuid(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        Guid guid = null;
        while (eventReader.hasNext()) {
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(GUID)) {
                guid = new Guid("true", null);
                Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                while (attributes.hasNext()) {
                    Attribute attribute = attributes.next();
                    if (attribute.getName().toString().equals(IS_PERM_LINK)) {
                        guid.setIsPermLink(attribute.getValue());
                    }
                }
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(GUID)) {
                break;
            } else {
                if (event instanceof Characters) {
                    guid.setValue(event.asCharacters().getData());
                }
            }
            event = eventReader.nextEvent();
        }
        return guid;
    }

    public synchronized void setlist(Channel channel) {
        output.add(channel);
    }

    public synchronized ArrayList<Channel> getlist() {
        return output;
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
        Channel channel = null;
        for (URL url : this.url)
            channel = fetchFeed(url);
	}


}
