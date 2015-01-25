package se.leafcoders.rosette.application;

public class ApplicationSettings {
	private boolean isLocked;

	private boolean useUploadCacheMaxAge;

	public ApplicationSettings lock() {
		isLocked = true;
		return this;
	}
	
	public boolean useUploadCacheMaxAge() {
		return useUploadCacheMaxAge;
	}

	public ApplicationSettings enableUploadCacheMaxAge() {
		if (!isLocked) {
			useUploadCacheMaxAge = true;
		}
		return this;
	}
}
