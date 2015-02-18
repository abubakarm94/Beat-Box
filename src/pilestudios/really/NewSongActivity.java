package pilestudios.really;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import pilestudios.async.globalData;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class NewSongActivity extends Activity implements OnClickListener {

	private static final int SELECT_AUDIO = 2;
	String selectedPath = "";
	private Uri fileUri;
	private ParseFile image;
	private ParseFile thumbnail;
	// private Photo photo;
	private ParseFile song;
	private Uri selectedAudioUri;
	private String songPath;
	private String songTitle;
	private long albumId;
	private Photo photo;
	private String artistName;

	private boolean publicUpload = false, privateUpload = false;
	private int fileSize;

	private final int permittedSpace = 250;

	// permitts whether or not the user can upload. upload is allowed if the
	// user meets all the requirment.
	private boolean shouldUpload = true;
	private Number songCount;
	private globalData gData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		// openGalleryAudio();

		// After taking a picture, open the NewPhotoFragment
		/*
		 * android.app.FragmentManager manager = getFragmentManager();
		 * android.app.Fragment fragment =
		 * manager.findFragmentById(R.id.fragmentContainer);
		 * 
		 * if (fragment == null) { fragment = new NewPhotoFragment(); // add id
		 * of the FrameLayout to fill, and the fragment that the layout will
		 * hold manager.beginTransaction().add(R.id.fragmentContainer, fragment)
		 * .commit(); }
		 */
		TextView privateUplink = (TextView) findViewById(R.id.private_upload);
		TextView publicUplink = (TextView) findViewById(R.id.public_upload);
		TextView cancelButton = (TextView) findViewById(R.id.cancel_upload);
		publicUplink.setOnClickListener(this);
		privateUplink.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		photo = new Photo();
	}

	public void openGalleryAudio() {

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(intent, SELECT_AUDIO);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			if (requestCode == SELECT_AUDIO) {
			//	setContentView(R.layout.fragment_new_photo);

				System.out.println("SELECT_AUDIO");
				selectedAudioUri = data.getData();
				selectedPath = selectedAudioUri.getPath();
				System.out.println("SELECT_AUDIO Path : " + selectedPath);
				// doFileUpload();
				songPath = getPathFromUri(getApplicationContext(),
						selectedAudioUri);
				songTitle = getSongTitle(selectedAudioUri);
				artistName = artistName(selectedAudioUri);
				if (artistName == null) {
					artistName = "Unknown";
				}
				fileSize = getFileSize(selectedAudioUri);
				// songCount =getCurrentPhoto().getUploadCount();
				
				 gData = globalData.getInstance(this);
				 int usedSpace = gData.getUsedSpace();
				
				 songCount = gData.getUploadCount();
				 
			
				  if ((usedSpace + fileSize) >= gData.getTotalSpace()) {
					shouldUpload = false;
				}else{
					shouldUpload = true;
				}
				
			

				// Toast.makeText(getApplicationContext(), usedSpace.intValue()+"",
				// 4).show();

				// Bitmap tempUri =
				// getSongCoverArt(getApplicationContext(),selectedAudioUri);
				if (shouldUpload == true) {
					Bitmap tempUri = getArt(songPath);
					if (tempUri == null) {
						
					} else {
						
						savePhotoFiles(tempUri, selectedAudioUri);

					}
				}else{
					Toast.makeText(getApplicationContext(), "Permitted Space Exceeded", 7).show();
					//setContentView(R.layout.activity_upload);
					finish();

				}
			}
			Toast.makeText(getApplicationContext(), "Uploading!", Toast.LENGTH_LONG).show();
			finish();

		}
	}

	public Bitmap getArt(String path) {
		Bitmap songImage = null;
		MediaMetadataRetriever metaRetriver;
		metaRetriver = new MediaMetadataRetriever();
		metaRetriver.setDataSource(path);
		byte[] artBytes = metaRetriver.getEmbeddedPicture();
		if (artBytes != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0,
					artBytes.length);

			// imgArt.setImageBitmap(bm);
			return bitmap;
		} else {
			// imgArt.setImageDrawable(getResources().getDrawable(R.drawable.no_cover_art));
			Bitmap imgArt = BitmapFactory.decodeResource(
					getApplicationContext().getResources(),
					R.drawable.no_cover_art);
			return imgArt;
		}
	}

	public int getFileSize(Uri uri) {
		// gets the size of the file
		ContentResolver cr = getContentResolver();
		InputStream is = null;
		int size = 0;
		try {
			is = cr.openInputStream(uri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			size = is.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fileSize = android.text.format.Formatter.formatShortFileSize(
				this, size);

		int sizef = (int) Float.parseFloat(fileSize.substring(0,
				fileSize.length() - 2));
		return sizef;
	}

	public String getSongTitle(Uri uri) {
		Cursor returnCursor = getContentResolver().query(uri, null, null, null,
				null);
		/*
		 * Get the column indexes of the data in the Cursor, move to the first
		 * row in the Cursor, get the data, and display it.
		 */
		int nameIndex = returnCursor
				.getColumnIndex(OpenableColumns.DISPLAY_NAME);

		int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

		// albumId =
		// returnCursor.getLong(returnCursor.getColumnIndex(MediaStore.Audio.Albums._ID));

		returnCursor.moveToFirst();

		return returnCursor.getString(nameIndex);
	}

	public String artistName(Uri uri) {
		String artist_name;

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(this, uri);
		String album = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		//Toast.makeText(this, album, Toast.LENGTH_SHORT).show();

		return album;
	}

	public static String getPathFromUri(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/*** Getters ***/
	public Uri getPhotoFileUri() {
		return fileUri;
	}

	public ParseFile getImageFile() {
		return image;
	}

	public ParseFile getThumbnailFile() {
		return thumbnail;
	}

	public Photo getCurrentPhoto() {
		return photo;
	}

	private void savePhotoFiles(Bitmap anypicImage, Uri uri) {

		// Convert to Bitmap to assist with resizing
		// Bitmap anypicImage = decodeSampledBitmapFromFile(pathToFile, 560,
		// 560);
		// Bitmap anypicImage = BitmapFactory.decodeByteArray(data, 0,
		// data.length);

		// Override Android default landscape orientation and save portrait
		Matrix matrix = new Matrix();
		// matrix.postRotate(90);
		Bitmap rotatedImage = Bitmap.createBitmap(anypicImage, 0, 0,
				anypicImage.getWidth(), anypicImage.getHeight(), matrix, true);

		// make thumbnail with size of 86 pixels
		Bitmap anypicThumbnail = Bitmap.createScaledBitmap(anypicImage, 86, 86,
				false);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		byte[] rotatedData = bos.toByteArray();

		bos.reset(); // reset the stream to prepare for the thumbnail
		anypicThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		byte[] thumbnailData = bos.toByteArray();

		try {
			// close the byte array output stream
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create the ParseFiles and save them in the background
		image = new ParseFile("photo.jpg", rotatedData);
		song = new ParseFile("song."
				+ songPath.substring(songPath.length() - 3),
				convertAudioToByte(uri));
		thumbnail = new ParseFile("photo_thumbnail.jpg", thumbnailData);
		image.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getApplicationContext(),
							"Error saving image file: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				} else {
					// saved image to Parse
					getCurrentPhoto().setImage(image);
					getCurrentPhoto().setThumbnail(thumbnail);
					getCurrentPhoto().setSong(song);
					//getCurrentPhoto().setUploadCount((songCount.intValue()+1));
					gData.setUploadCount(1+gData.getUploadCount());
					ParseUser.getCurrentUser().put("uploadCount",gData.getUploadCount());
					ParseUser.getCurrentUser().saveInBackground();

					if (publicUpload) {
						getCurrentPhoto().setVisiblity(publicUpload);
					} else {
						getCurrentPhoto().setVisiblity(privateUpload);

					}
					getCurrentPhoto().setSongTitle(songTitle);
					getCurrentPhoto().setArtistName(artistName);
					getCurrentPhoto().setUser(ParseUser.getCurrentUser());
					getCurrentPhoto().setSongSize(fileSize);

					gData.setUsedSpace(gData.getUsedSpace()+fileSize);
					ParseUser.getCurrentUser().put("usedSpace",gData.getUsedSpace());
					ParseUser.getCurrentUser().saveInBackground();
					//getCurrentPhoto().setUsedSpace(
							//(value.intValue() + fileSize));
					doe();

				}
			}
		});

		Log.i("dsd", "Finished saving the photos to ParseFiles!");

	}

	public void doe() {

		photo.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// setResult(Activity.RESULT_OK);
					// finish();
					Toast.makeText(getApplicationContext(), "Sucess", 5)
							.show();
					//finish();

				} else {
					Toast.makeText(getApplicationContext(),
							"Error saving: " + e.getMessage(), 5).show();
				}
			}

		});
	}

	public byte[] convertAudioToByte(Uri uri) {
		InputStream iStream = null;
		try {
			iStream = getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] inputData = null;
		try {
			inputData = getBytes(iStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return inputData;
	}

	public byte[] getBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.private_upload:
			// Toast.makeText(getApplicationContext(), "private", 6).show();
			publicUpload = false;
			openGalleryAudio();
			break;
		case R.id.public_upload:
			// Toast.makeText(getApplicationContext(), "public", 6).show();
			publicUpload = true;
			openGalleryAudio();

			break;

		case R.id.cancel_upload:

			finish();

			break;

		}
	}

}
