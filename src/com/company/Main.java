package com.company;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.io.RandomAccessFile;

public class Main {
    private static int totalSongs=0;
    private static int albumsRetreived=0;
    private static boolean isAlbumartFound=false;
    public static void main(String[] args) {
        String current = System.getProperty("user.dir");
        try{
            File folder = new File(current);
            File[] listOfFiles = folder.listFiles();
            File temp;
            ImagesFetcher fetcher = new ImagesFetcher();
            String artist, album, title;
            System.out.println("__________________________");
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".mp3")) {
                    totalSongs++;
                    System.out.println("File " + listOfFiles[i].getName());//retrieve song
                    Mp3File f = new Mp3File(listOfFiles[i].getName());
                    ID3v2 tag;
                    if (f.hasId3v2Tag()) {
                        tag = f.getId3v2Tag();
                    } else {
                        // mp3 does not have an ID3v2 tag, let's create one..
                        tag = new ID3v24Tag();
                        f.setId3v2Tag(tag);
                    }
                    title = tag.getTitle();
                    album = tag.getAlbum();
                    artist = tag.getArtist();

                    System.out.println("Artist: "+artist);
                    System.out.println("Title: "+title);

                    byte[] albumImageData = tag.getAlbumImage();
                    if (albumImageData != null) {
                        System.out.println("Song has artwork already.");
                    }
                    else{
                        String x =fetcher.MBIDFinder(artist, title, album);
                        boolean isFound = fetcher.getAlbumArt(fetcher.albumArtUrl(x));
                        if(isFound){
                            isAlbumartFound = true;
                            albumsRetreived++;
                            RandomAccessFile image = new RandomAccessFile("a.jpg", "r");
                            byte[] imageasbyte = new byte[(int)image.length()];
                            image.read(imageasbyte);
                            image.close();

                            tag.setAlbumImage(imageasbyte,"image/jpeg");
                            f.save("song.mp3");

                            Mp3File finalmp3 = new Mp3File("song.mp3");
                            finalmp3.save(listOfFiles[i].getName());

                            temp = new File("song.mp3");
                            temp.delete();
                        }
                        else{
                            System.out.println("Album art for "+title+" not found\n" +
                                    "Searcing for artist image");
                            String artistImageURL =fetcher.getArtistImageUrl(artist);
                            if(artistImageURL.compareTo("false") == 0){
                                System.out.println("Artist image not found either");
                            }
                            else{
                                albumsRetreived++;
                                fetcher.getArtistImage(fetcher.getArtistImageUrl(artist));
                                RandomAccessFile image = new RandomAccessFile("b.jpg", "r");
                                byte[] imageasbyte = new byte[(int)image.length()];
                                image.read(imageasbyte);
                                image.close();

                                tag.setAlbumImage(imageasbyte,"image/jpeg");
                                f.save("song.mp3");

                                Mp3File finalmp3 = new Mp3File("song.mp3");
                                finalmp3.save(listOfFiles[i].getName());

                                temp = new File("song.mp3");
                                temp.delete();
                            }
                        }
                    }
                    System.out.println("__________________________");
                }

            }
            System.out.println("Total song: "+totalSongs);
            System.out.println("Album artworks retreived: "+albumsRetreived);
            File artworkTemp = new File("a.jpg");
            if(artworkTemp.exists())
                artworkTemp.delete();
        }
        catch(Exception e){
            System.out.println("ERROR: "+e);
        }


    }

}
