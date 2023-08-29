package yasmin.santana.rodrigues.galeriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryRepository {

    Context context;

    public GalleryRepository(Context context){
        this.context = context;
    } //quantidade de imagens carregada na tela, indo de 10 em 10 por ex

    public List<ImageData> loadImageData(Integer limit, Integer offset) throws FileNotFoundException{
        List<ImageData> imageDataList = new ArrayList<>();
        int w = (int)context.getResources().getDimension(R.dimen.im_width); //dimensao (altura largura)
        int h = (int)context.getResources().getDimension(R.dimen.im_height);

        String[] projection = new String[]{MediaStore.Images.Media._ID,//acessa o id das fotos dentro da galeria
        MediaStore.Images.Media.DISPLAY_NAME, //nome do arquivo da foto
        MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.SIZE}; //data da foto e tamanho em byte
        String selection = null;
        String selectionArgs[] = null;
        String sort = MediaStore.Images.Media.DATE_ADDED; //arquivos organizados de acordo com as datas

        Cursor cursor = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){ //versao 11 do android; pega um budle (guarda tuplas) e acessa a tabela de fotos do celular do usuáeio
            Bundle queryArgs = new Bundle();

            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);

            queryArgs.putString(ContentResolver.QUERY_ARG_GROUP_COLUMNS, sort);
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);

            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offset);

            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, queryArgs, null); //pega os parametros da consulta e deixa em cursor

        }
        else{
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
            selection, selectionArgs, sort + " ASC + LIMIT " + String.valueOf((limit) + " OFFSET " + String.valueOf(offset)));

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while (cursor.moveToNext()){
                long id = cursor.getLong(idColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id); //onstrui endereço uri para foto
                String name = cursor.getString(nameColumn); //pega nome, data etc
                int dateAdded = cursor.getInt(dateAddedColumn);
                int size = cursor.getInt(sizeColumn);
                Bitmap thumb = Util.getBitmap(context, contentUri, w, h); //cria uma fotinha

                imageDataList.add(new ImageData(contentUri, thumb, name,
                        new Date(dateAdded*1000L), size));
            }
        }
        return imageDataList; //contem apenas quantidade de itens
    }
}
