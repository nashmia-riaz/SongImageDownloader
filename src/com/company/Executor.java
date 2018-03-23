package com.company;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by nashm on 11/12/2017.
 */
public class Executor {
    private static int totalSongs=0;
    private static int albumsRetreived=0;

    public static void main(String[] args) {
        String current = System.getProperty("user.dir");
        ID3v2 tag;
        String artist = null;
        String album = null;
        String title = null;
        Mp3File song = null;
        String currentFile = "";
        ImagesFetcher fetcher1 = new ImagesFetcher();
        File folder = new File(current);
        File[] listOfFiles = folder.listFiles();
        int totalSongs=0;
        for(int i = 0; i<listOfFiles.length; i++){
            currentFile = listOfFiles[i].getName();
            if(currentFile.endsWith(".mp3") && listOfFiles[i].isFile()){
                totalSongs++;
                try{
                    song = new Mp3File(currentFile);
                    if (song.hasId3v2Tag()) {
                        tag = song.getId3v2Tag();
                    } else {
                        // mp3 does not have an ID3v2 tag, let's create one..
                        tag = new ID3v24Tag();
                        song.setId3v2Tag(tag);
                    }
                    artist = tag.getArtist();
                    album = tag.getAlbum();
                    title = tag.getTitle();

                    System.out.println(artist+" - "+title);
                    String albumArtURL = fetcher1.albumArtURLDeezer(artist, album);
                    byte[] hasAlbumArt = tag.getAlbumImage();
                    if(hasAlbumArt!=null){
                        System.out.println("Song already has art");
                    }
                    else{
                        System.out.println("Searching Deezer");
                        if(albumArtURL.compareTo("false")==0 || albumArtURL.compareTo("")==0){
                            System.out.println("Album art not found on Deezer\n" +
                                    "Searching LastFM");
                            albumArtURL = fetcher1.getLastFMAlbumURL(artist, title);
                            if(albumArtURL.compareTo("false")==0 || albumArtURL.compareTo("")==0){
                                System.out.println("No art found on LastFM\n" +
                                        "Searching CoverArt for album image");
                                if(albumArtURL.compareTo("false")==0 || albumArtURL.compareTo("")==0) {
                                    System.out.println("No art found on Cover Art Archive\n" +
                                            "Searching LastFM for artist image");
                                    albumArtURL = fetcher1.getLastFMArtistURL(artist);
                                    if(albumArtURL.compareTo("false")==0 || albumArtURL.compareTo("")==0){
                                        System.out.println("No artist image found either");
                                    }
                                    else{
                                        System.out.println("Art found on lastFM");
                                        albumsRetreived++;
                                        fetcher1.saveImage(albumArtURL);
                                    }
                                }
                                else{
                                    System.out.println("Album art found Cover art archive");
                                    albumsRetreived++;
                                    fetcher1.saveImage(albumArtURL);
                                }
                            }
                            else{
                                System.out.println("Album image found on lastfm");
                                albumsRetreived++;
                                fetcher1.saveImage(albumArtURL);
                            }
                        }else{
                            albumsRetreived++;
                            System.out.println("Album image found on Deezer");
                            fetcher1.saveImage(albumArtURL);
                        }

                        fetcher1.updateTag(artist, title, album, song, currentFile);
                    }
                } catch (UnsupportedTagException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidDataException e) {
                    e.printStackTrace();
                }
            }
            //clean up
            File tempSong = new File("song.mp3");
            if(tempSong.exists())
                tempSong.delete();
            File tempImage = new File("a.png");
            if(tempImage.exists())
                tempImage.delete();
        }
        System.out.println("Art retrieved: "+albumsRetreived+"/"+totalSongs);
    }
}
