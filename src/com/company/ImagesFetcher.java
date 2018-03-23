package com.company;

import com.mpatric.mp3agic.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by nashm on 08/12/2017.
 */
public class ImagesFetcher {
    private String[] imageTypes = {"cover_xl", "cover_big", "cover_medium", "cover_small"};

    public String MBIDFinder(String artist, String song, String album) {
        try{
            String [] artists = artist.split(",");

            if(artists.length>1){
                artist = '"'+artists[0]+'"';
            }
            else
                artist ='"'+artist+'"';

            album = '"' + album +'"';
            song ='"'+song+'"';
            artist=URLEncoder.encode(artist, "UTF-8");
            album=URLEncoder.encode(album, "UTF-8");
            song=URLEncoder.encode(song, "UTF-8");
            artist = artist.replace("+","%20");
            album = album.replace("+","%20");
            song = song.replace("+","%20");

            //generate link
            String link = "http://www.musicbrainz.org/ws/2/releases/?query=artist:"+artist+"+recording:"+song+"+dismax=true&fmt=json";
//            System.out.println(link);
            URL website = new URL (link);
            URLConnection conn = website.openConnection();
            conn.setRequestProperty("Content-Type", "xml");

            InputStream in =  website.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }
            String xml = builder.toString();
//            System.out.println(xml);
            JSONObject json = new JSONObject(xml);
            JSONArray array = (JSONArray) json.get("releases");
            if(array.length()>0){
                JSONObject firstel = (JSONObject) array.get(0);
                String id = (String) firstel.get("id");
                return id;
            }
            else
                return "false";
        }
        catch (IOException  e){
            return "false";
        }
    }


    public String albumArtUrl(String ubid) {
        try{
            String link = "http://coverartarchive.org/release/"+ubid+"/";
            URL website = new URL (link);
            URLConnection conn = website.openConnection();

            InputStream in =  website.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }
            String xml = builder.toString();

            JSONObject json = new JSONObject(xml);

            JSONArray images = (JSONArray) json.get("images");
            JSONObject image = (JSONObject) images.get(0);
//            String imageurl = image.getString("image");
            JSONObject thumbnails = (JSONObject) image.get("thumbnails");
            String imageurl = (String) thumbnails.get("large");
//            System.out.println(imageurl);
            in.close();
            return imageurl;
        }catch(IOException e){
            return "false";
        }

    }

    public boolean saveImage(String url) {
        try{
            URL albumartUrl = new URL (url);
            URLConnection conn = albumartUrl.openConnection();

            InputStream in =  albumartUrl.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }

            ImageIO.write(ImageIO.read(albumartUrl), "png", new File("a.png"));

            //resize file
//            BufferedImage originalImage = ImageIO.read(new File("a.png"));
//            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
//            BufferedImage resizeImageJpg = resizeImage(originalImage, type);
//            ImageIO.write(resizeImageJpg, "png", new File("a.png"));

            in.close();
            return true;
        }catch(IOException e){
            return false;
        }
    }

    public String getLastFMArtistURL(String artist){
        try{
            String[] artistsString = artist.split(",");
            if(artistsString.length>0)
                artist=artistsString[0];

            artist = URLEncoder.encode(artist, "UTF-8");
            artist = artist.replace("+", "%20");
            String link = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist="+artist+"&api_key=a7a901f5229206a59cbaa4293d6f302b&format=json";
//            System.out.println(link);
//            String link = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist="+artist+"&api_key=a7a901f5229206a59cbaa4293d6f302b&format=json";
//            System.out.println(link);
            URL website = new URL (link);
            URLConnection conn = website.openConnection();

            InputStream in =  website.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }
            String xml = builder.toString();
            JSONObject json = new JSONObject(xml);
            JSONObject results = (JSONObject) json.get("results");
            JSONObject artistmatches = (JSONObject) results.get("artistmatches");
            JSONArray artists = (JSONArray) artistmatches.get("artist");
            if(artists.length()>0){
                JSONObject artistResult = (JSONObject) artists.get(0);
                JSONArray image = (JSONArray) artistResult.get("image");
                int l = image.length();
                JSONObject images = (JSONObject) image.get(l-1);
                String url = images.getString("#text");
                return url;
            }
            else
                return "false";
        }catch(IOException e){
            return "false";
        }
    }

    public String getLastFMAlbumURL(String artist, String track){
        try{
            artist = URLEncoder.encode(artist, "UTF-8");
            artist = artist.replace("+", "%20");
            track = URLEncoder.encode(track, "UTF-8");
            track = track.replace("+", "%20");
            String link = "http://ws.audioscrobbler.com/2.0/?method=track.search&artist="+artist+"&track="+track+"&api_key=a7a901f5229206a59cbaa4293d6f302b&format=json";
//            String link = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist="+artist+"&api_key=a7a901f5229206a59cbaa4293d6f302b&format=json";
//            System.out.println(link);
            URL website = new URL (link);
            URLConnection conn = website.openConnection();

            InputStream in =  website.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }
            String xml = builder.toString();
            JSONObject json = new JSONObject(xml);
            JSONObject results = (JSONObject) json.get("results");
            JSONObject artistmatches = (JSONObject) results.get("trackmatches");
            JSONArray artists = (JSONArray) artistmatches.get("track");
            if(artists.length()>0){
                JSONObject artistResult = (JSONObject) artists.get(0);
                JSONArray image = (JSONArray) artistResult.get("image");
                int l = image.length();
                JSONObject images = (JSONObject) image.get(l-1);
                String url = images.getString("#text");
                return url;
            }
            else
                return "false";
        }catch(IOException e){
            return "false";
        }
    }

    public void overwriteSong(String filename){
        Mp3File finalmp3 = null;
        try {
            finalmp3 = new Mp3File("song.mp3");
            finalmp3.save(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }catch(NotSupportedException e){
            e.printStackTrace();
        }
    }

    public byte[] getImage(){
        RandomAccessFile image = null;
        try {
            image = new RandomAccessFile("a.png", "r");
            byte[] imageasbyte = new byte[(int) image.length()];
            image.read(imageasbyte);
            image.close();
            return imageasbyte;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            byte[] a = new byte[1];
            a[0]=0;
            return a;
        }
        catch(IOException e){
            e.printStackTrace();
            byte[] a = new byte[1];
            a[0]=0;
            return a;
        }
    }
    private static BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(1000, 1000, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 1000, 1000, null);
        g.dispose();
        return resizedImage;
    }

    public boolean updateTag(String artist, String title, String album, Mp3File song, String currentFile){
        ID3v2 tag = new ID3v24Tag();
        tag.setAlbum(album);
        tag.setArtist(artist);
        tag.setTitle(title);
//        System.out.println(album);
//        System.out.println(artist);
        song.setId3v2Tag(tag);
        try {
            byte[] albumArt = getImage();

            tag.setAlbumImage(albumArt,"image/png");
            song.setId3v2Tag(tag);
            song.save("song.mp3");

            overwriteSong(currentFile);

            System.out.println("__________________________");
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (NotSupportedException e1) {
            e1.printStackTrace();
            return false;
        }
    }
    public String albumArtURLDeezer(String artist, String track){
        try {
            artist = URLEncoder.encode(artist, "UTF-8");
            track = URLEncoder.encode(track, "UTF-8");
            String link = "https://api.deezer.com/search/?q="+artist+"+"+track+"&format=json";
//            System.out.println(link);
            URL website = new URL (link);
            URLConnection conn = website.openConnection();
            conn.setRequestProperty("Content-Type", "xml");

            InputStream in =  website.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }
            String xml = builder.toString();

            JSONObject json = new JSONObject(xml);
            JSONArray data = (JSONArray) json.get("data");
            if(data.length()>0){
                JSONObject result = (JSONObject) data.get(0);
                JSONObject album = (JSONObject) result.get("album");
                String albumURL = null;
                int i =0;
                while(albumURL == null){
                    albumURL = album.getString(imageTypes[i]);
                    i++;
                }
                return albumURL;
            }
            else{
                return "false";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "false";
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
}
