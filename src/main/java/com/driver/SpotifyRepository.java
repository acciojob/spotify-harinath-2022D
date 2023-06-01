package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newuser = new User(name,mobile);
        users.add(newuser);
        return newuser;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Optional<Artist> check_present_or_not(String name){
        for(int i=0; i<artists.size(); i++){
            String name1 = artists.get(i).getName();
            if(name1.equals(name)){
                return Optional.of(artists.get(i));
            }
        }
        return Optional.empty();
    }
    public Album createAlbum(String title, String artistName) {
//        Album album = new Album(title);
//        boolean flag = false;
//        for(int i=0; i<artists.size(); i++){
//            String name = artists.get(i).getName();
//            if(name == artistName){
//                flag = true;
//                break;
//            }
//        }
//        if(flag == false){
//            Artist artist = createArtist(artistName);
//        }
//        albums.add(album);
//        return album;
        Album album = new Album(title);
        Artist artist = null;
        Optional<Artist> optionalArtist = check_present_or_not(artistName);
        if(optionalArtist.isEmpty()){
             artist = createArtist(artistName);
        }
        albums.add(album);
        List<Album> albumList = artistAlbumMap.getOrDefault(artist,new ArrayList<>());
        albumList.add(album);
        artistAlbumMap.put(artist,albumList);
        return album;
    }

    public Optional<Album> find_albun(String title){

        for(int i=0; i<albums.size(); i++){
            String album_name = albums.get(i).getTitle();
            if(album_name.equals(title)){
                return Optional.of(albums.get(i));
            }
        }
        return Optional.empty();
    }
    public Song createSong(String title, String albumName, int length) throws Exception{
        Optional<Album> optionalAlbum = find_albun(albumName);
        if(optionalAlbum.isEmpty()){
            throw new Exception("Album does not exist");
        }
        Album album = optionalAlbum.get();
        Song song = new Song(title,length);
        songs.add(song);
        if(albumSongMap.containsKey(album)){
            List<Song> songList = albumSongMap.get(album);
            songList.add(song);
            albumSongMap.put(album,songList);
        }else{
            List<Song> songList = new ArrayList<>();
            songList.add(song);
            albumSongMap.put(album,songList);
        }
        return song;

    }

    public Optional<User> find_user(String mobile){
        for(int i=0; i<users.size(); i++){
            String mobile_num = users.get(i).getMobile();
            if(mobile_num.equals(mobile)){
                return Optional.of(users.get(i));
            }
        }
        return Optional.empty();
    }
    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Optional<User> optionalUser = find_user(mobile);
        if(optionalUser.isEmpty()){
            throw new Exception("User does not exist");
        }
        User user = optionalUser.get();
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        // 1.adding songs to the palylist
        List<Song> songList = new ArrayList<>();
        for(int i=0; i<songs.size(); i++){
            int songlen = songs.get(i).getLength();
            if(songlen == length){
                songList.add(songs.get(i));
            }
        }
        playlistSongMap.put(playlist,songList);

        //2.user -> playlist creater
        creatorPlaylistMap.put(user,playlist);

        //3.playlist -> listener
        List<User> listenerList = new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);

        //4 user -> no.of playlists
        if(userPlaylistMap.containsKey(user)){
            List<Playlist> playlistList = userPlaylistMap.get(user);
            playlistList.add(playlist);
            userPlaylistMap.put(user,playlistList);
        }else{
            List<Playlist> playlistList = new ArrayList<>();
            playlistList.add(playlist);
            userPlaylistMap.put(user,playlistList);
        }

        return playlist;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Optional<User> optionalUser = find_user(mobile);
        if(optionalUser.isEmpty()){
            throw new Exception("User does not exist");
        }
        User user = optionalUser.get();
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        // 1.adding songs to the palylist
        List<Song> songList = new ArrayList<>();
        for(int i=0; i<songs.size(); i++){
            String songtitle = songs.get(i).getTitle();
            if(songTitles.contains(songtitle)){
                songList.add(songs.get(i));
            }
        }
        playlistSongMap.put(playlist,songList);

        //2.user -> playlist creater
        creatorPlaylistMap.put(user,playlist);

        //3.playlist -> listener
        List<User> listenerList = new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);

        //4 user -> no.of playlists
        if(userPlaylistMap.containsKey(user)){
            List<Playlist> playlistList = userPlaylistMap.get(user);
            playlistList.add(playlist);
            userPlaylistMap.put(user,playlistList);
        }else{
            List<Playlist> playlistList = new ArrayList<>();
            playlistList.add(playlist);
            userPlaylistMap.put(user,playlistList);
        }

        return playlist;
    }

    public Optional<Playlist> find_playlist(String title){
        for(int i=0; i<playlists.size(); i++){
            String name = playlists.get(i).getTitle();
            if(name.equals(title)){
                return Optional.of(playlists.get(i));
            }
        }
        return Optional.empty();
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        // check user
        Optional<User> optionalUser = find_user(mobile);
        if(optionalUser.isEmpty()){
            throw new Exception("User does not exist");
        }
        // check playlist
        Optional<Playlist> optionalPlaylist = find_playlist(playlistTitle);
        if(optionalPlaylist.isEmpty()){
            throw new Exception("Playlist does not exist");
        }

        User user = optionalUser.get();
        Playlist playlist = optionalPlaylist.get();
        // check creater
        if(creatorPlaylistMap.containsKey(user)) return playlist;

        List<User> listnerlist = playlistListenerMap.get(playlist);
        if(listnerlist.contains(user)) return playlist;
        else{
            listnerlist.add(user);
            playlistListenerMap.put(playlist,listnerlist);
        }

        return playlist;
    }

    public Optional<Song> find_song(String title){
        for(int i=0; i<songs.size(); i++){
            String name = songs.get(i).getTitle();
            if(name.equals(title)){
                return Optional.of(songs.get(i));
            }
        }
        return Optional.empty();
    }

    public Album find_album_of_song(Song song){
        for(Album album : albumSongMap.keySet() ){
            List<Song> songList = albumSongMap.get(album);
            if(songList.contains(song)){
                return album;
            }
        }
        return null;

    }

    public Artist find_artist_of_album(Album album){
        for(Artist artist : artistAlbumMap.keySet()){
            List<Album> albumList = artistAlbumMap.get(artist);
            if(albumList.contains(album)){
                return artist;
            }
        }
        return null;
    }
    public Song likeSong(String mobile, String songTitle) throws Exception {
        // chesk user
        Optional<User> optionalUser = find_user(mobile);
        if(optionalUser.isEmpty()){
            throw new Exception("User does not exist");
        }
        // check song
        Optional<Song> optionalSong = find_song(songTitle);
        if(optionalSong.isEmpty()){
            throw new Exception("Song does not exist");
        }
        // check song like map
        User user = optionalUser.get();
        Song song = optionalSong.get();
        if(songLikeMap.containsKey(song)){
            List<User> userList = songLikeMap.get(song);
            if(userList.contains(user)) return song;
            else{
                userList.add(user);
                songLikeMap.put(song,userList);
                //update song likes
                song.setLikes(song.getLikes()+1);
                //update artist likes
                // song -> album -> artist
                //find album of song
                Album album = find_album_of_song(song);
                //fing artist with album name
                Artist artist = find_artist_of_album(album);
                artist.setLikes(artist.getLikes()+1);
            }
        }else{
            List<User> userList = new ArrayList<>();
            userList.add(user);
            songLikeMap.put(song,userList);
            //update song likes
            song.setLikes(song.getLikes()+1);
            //update artist likes
            // song -> album -> artist
            //find album of song
            Album album = find_album_of_song(song);
            //fing artist with album name
            Artist artist = find_artist_of_album(album);
            artist.setLikes(artist.getLikes()+1);
        }

        return song;
    }

    public String mostPopularArtist() {
        int maxlikes = 0;
        String artistName = "";
        for(Artist artist : artists){
            int likes = artist.getLikes();
            if(likes >= maxlikes){
                maxlikes = likes;
                artistName = artist.getName();
            }
        }
        return artistName;
    }

    public String mostPopularSong() {
        int maxlikes = 0;
        String songName = "";
        for(Song song : songs){
            int likes = song.getLikes();
            if(likes >= maxlikes){
                maxlikes = likes;
                songName = song.getTitle();
            }
        }
        return songName;
    }
}
