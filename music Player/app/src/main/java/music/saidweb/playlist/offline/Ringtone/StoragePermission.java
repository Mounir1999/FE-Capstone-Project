package music.saidweb.playlist.offline.Ringtone;

public interface StoragePermission {
    void storageValidation(boolean isStorageGranted);
    void setRingtone();
}