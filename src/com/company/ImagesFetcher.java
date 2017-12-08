package com.company;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by nashm on 08/12/2017.
 */
public class ImagesFetcher {
    public String MBIDFinder(String artist, String song, String album) {
        try{
//            artist ='"'+artist+'"';
//            album = '"' + album +'"';
//            song ='"'+song+'"';
            artist=URLEncoder.encode(artist, "UTF-8");
            album=URLEncoder.encode(album, "UTF-8");
            song=URLEncoder.encode(song, "UTF-8");
            artist = artist.replace("+","%20");
            album = album.replace("+","%20");
            song = song.replace("+","%20");

            //generate link
            String link = "http://www.musicbrainz.org/ws/2/release/?query=artist:"+artist+"+album:"+album+"+~&dismax=true&fmt=json";
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
            JSONArray array = (JSONArray) json.get("releases");
            if(array.length()>0){
                JSONObject firstel = (JSONObject) array.get(1);
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
            JSONObject thumbnails = (JSONObject) image.get("thumbnails");
            String imageurl = (String) thumbnails.get("large");
//            System.out.println(imageurl);
            in.close();
            return imageurl;
        }catch(IOException e){
            return "false";
        }

    }

    public boolean getAlbumArt(String url) {
        try{
            URL albumartUrl = new URL (url);
            URLConnection conn = albumartUrl.openConnection();

            InputStream in =  albumartUrl.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr=in.read())!=-1){
                builder.append((char)ptr);
            }

            ImageIO.write(ImageIO.read(albumartUrl), "jpg", new File("a.jpg"));

            in.close();
            return true;
        }catch(IOException e){
            return false;
        }
    }

    public String getArtistImageUrl(String artist){
        try{
            artist = artist.replace(" ", "%20");
            String link = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist="+artist+"&api_key=a7a901f5229206a59cbaa4293d6f302b&format=json";
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
                JSONObject images = (JSONObject) image.get(3);
                String url = images.getString("#text");
                return url;
            }
            else
                return "false";
        }catch(IOException e){
            return "false";
        }
    }

    public boolean getArtistImage(String url){
            try{
                URL albumartUrl = new URL (url);
                URLConnection conn = albumartUrl.openConnection();

                InputStream in =  albumartUrl.openStream();
                int ptr = 0;
                StringBuilder builder = new StringBuilder();
                while((ptr=in.read())!=-1){
                    builder.append((char)ptr);
                }

                ImageIO.write(ImageIO.read(albumartUrl), "jpg", new File("b.jpg"));

                in.close();
                return true;
            }catch(IOException e) {
                return false;
            }
    }
}
