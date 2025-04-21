package com.example.apppet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apppet.models.LocationModel;
import com.example.apppet.network.ApiService;
import com.example.apppet.network.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker petMarker;
    private Handler handler = new Handler();

    private int collarId;
    private LatLng ubicacionCollar;
    private LatLng ultimaUbicacionGuardada = null;

    private LatLng geofenceCenter = new LatLng(4.60980, -74.08200);
    private float geofenceRadius = 100;
    private Circle geofenceCircle;

    private static final String CHANNEL_ID = "GEOFENCE_CHANNEL";
    private static final int NOTIFICATION_ID = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2001;

    private Button btnSetGeofence;

    private Polyline currentPolyline;

    private Marker startMarker, endMarker;
    FloatingActionButton btnAgregarPunto;
    private FloatingActionButton fabMenu, fabAgregarPunto, fabUltimasUbicaciones;
    private boolean isFabOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        createNotificationChannel();
        requestNotificationPermission();
        solicitarPermisoUbicacion();

        collarId = getIntent().getIntExtra("collar_id", 0);
        Log.d("MapActivity", "Collar ID recibido: " + collarId);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // FABs
        FloatingActionButton fabMain = findViewById(R.id.fabMain);
        FloatingActionButton fabAgregarPunto = findViewById(R.id.btnAgregarPuntoManual);
        FloatingActionButton fabUltimasUbicaciones = findViewById(R.id.btnUltimasUbicaciones);

        // Botón de geocerca (independiente del menú)
        btnSetGeofence = findViewById(R.id.btnSetGeofence);
        btnSetGeofence.setOnClickListener(v -> openGeofenceDialog());

        // Animaciones de FAB secundarias (al presionar el FAB principal)
        fabMain.setOnClickListener(v -> {
            if (fabAgregarPunto.getVisibility() == View.VISIBLE) {
                fabAgregarPunto.hide();
                fabUltimasUbicaciones.hide();
            } else {
                fabAgregarPunto.show();
                fabUltimasUbicaciones.show();
            }
        });

        fabAgregarPunto.setOnClickListener(v -> abrirDialogoAgregarPunto());
        fabUltimasUbicaciones.setOnClickListener(v -> mostrarUltimas5Ubicaciones());

        // Inicializar como ocultos
        fabAgregarPunto.hide();
        fabUltimasUbicaciones.hide();

        // Inicia actualizaciones de ubicación
        handler.postDelayed(actualizacionUbicacionRunnable, 10000);
        handler.postDelayed(guardarUbicacionCada5Min, 5 * 60 * 1000);
    }

    private void toggleFabMenu() {
        if (isFabOpen) {
            fabAgregarPunto.animate().translationY(0).alpha(0).withEndAction(() -> fabAgregarPunto.setVisibility(View.GONE));
            fabUltimasUbicaciones.animate().translationY(0).alpha(0).withEndAction(() -> fabUltimasUbicaciones.setVisibility(View.GONE));
        } else {
            fabAgregarPunto.setVisibility(View.VISIBLE);
            fabUltimasUbicaciones.setVisibility(View.VISIBLE);
            fabAgregarPunto.setAlpha(0f);
            fabUltimasUbicaciones.setAlpha(0f);
            fabAgregarPunto.animate().translationY(-getResources().getDimension(R.dimen.standard_55)).alpha(1);
            fabUltimasUbicaciones.animate().translationY(-getResources().getDimension(R.dimen.standard_105)).alpha(1);
        }
        isFabOpen = !isFabOpen;
    }
    private void mostrarUltimas5Ubicaciones() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.obtenerRutaCollar(collarId).enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationModel> lista = response.body();
                    int desde = Math.max(0, lista.size() - 5);
                    List<LocationModel> ultimas5 = lista.subList(desde, lista.size());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_ultimas_ubicaciones, null);
                    LinearLayout contenedor = dialogView.findViewById(R.id.contenedorUbicaciones);

                    for (LocationModel loc : ultimas5) {
                        TextView txt = new TextView(MapActivity.this);
                        txt.setText("📌 Lat: " + loc.getLatitud() + ", Lng: " + loc.getLongitud()
                                + "\n🕓 Hora: " + loc.getTimestamp());
                        txt.setPadding(0, 8, 0, 8);
                        txt.setTextSize(14f);
                        contenedor.addView(txt);
                    }

                    AlertDialog dialog = builder.setView(dialogView).create();

                    Button btnCerrar = dialogView.findViewById(R.id.btnCerrarDialogoUbicaciones);
                    btnCerrar.setOnClickListener(v -> dialog.dismiss());

                    dialog.show();
                } else {
                    Toast.makeText(MapActivity.this, "No se pudieron obtener las ubicaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void mostrarDialogoAgregarPunto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar punto manual");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_punto, null);
        EditText inputLat = viewInflated.findViewById(R.id.etLatManual);
        EditText inputLng = viewInflated.findViewById(R.id.etLngManual);

        builder.setView(viewInflated);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            try {
                double lat = Double.parseDouble(inputLat.getText().toString().trim());
                double lng = Double.parseDouble(inputLng.getText().toString().trim());



                LocationModel punto = new LocationModel();
                punto.setCollar_id(collarId);
                punto.setLatitud(lat);
                punto.setLongitud(lng);

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<Void> call = apiService.insertarUbicacionCollar(punto);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("UbicacionManual", "Ubicación insertada correctamente");
                            Toast.makeText(MapActivity.this, "Punto guardado en la BD", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("UbicacionManual", "Fallo al guardar: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("UbicacionManual", "Error de red: " + t.getMessage());
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void abrirDialogoAgregarPunto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Punto Manual");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_punto, null);
        EditText inputLat = viewInflated.findViewById(R.id.etLatManual);
        EditText inputLng = viewInflated.findViewById(R.id.etLngManual);

        builder.setView(viewInflated);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            try {
                double lat = Double.parseDouble(inputLat.getText().toString());
                double lng = Double.parseDouble(inputLng.getText().toString());

                LocationModel punto = new LocationModel();
                punto.setCollar_id(collarId);
                punto.setLatitud(lat);
                punto.setLongitud(lng);

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<Void> call = apiService.insertarUbicacionCollar(punto);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MapActivity.this, "✅ Punto guardado correctamente", Toast.LENGTH_SHORT).show();

                            // ✅ ACTUALIZAMOS EL RECORRIDO EN EL MAPA
                            mostrarRecorridoDelCollar(collarId);
                        } else {
                            Toast.makeText(MapActivity.this, "❌ Falló la inserción (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                            Log.e("AgregarPunto", "Error HTTP: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MapActivity.this, "⚠️ Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("AgregarPunto", "Error de red", t);
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "❗ Coordenadas inválidas", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }



    private void solicitarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1001);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        drawGeofenceCircle(geofenceCenter, geofenceRadius);
        if (collarId != 0) {
            obtenerUbicacionReal();
            mostrarRecorridoDelCollar(collarId);
        }
    }

    private final Runnable actualizacionUbicacionRunnable = new Runnable() {
        @Override
        public void run() {
            obtenerUbicacionReal();
            handler.postDelayed(this, 10000);
        }
    };

    private final Runnable guardarUbicacionCada5Min = new Runnable() {
        @Override
        public void run() {
            guardarUbicacionDelCollar();
            handler.postDelayed(this, 5 * 60 * 1000);
        }
    };

    private void guardarUbicacionDelCollar() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng nuevaUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                    if (ultimaUbicacionGuardada == null || distanciaEntre(nuevaUbicacion, ultimaUbicacionGuardada) > 5) {
                        ultimaUbicacionGuardada = nuevaUbicacion;

                        LocationModel ubicacion = new LocationModel();
                        ubicacion.setCollar_id(collarId);
                        ubicacion.setLatitud(nuevaUbicacion.latitude);
                        ubicacion.setLongitud(nuevaUbicacion.longitude);

                        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                        Call<Void> call = apiService.insertarUbicacionCollar(ubicacion);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d("Ubicacion", "Ubicación guardada con éxito.");
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("Ubicacion", "Fallo: " + t.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    private float distanciaEntre(LatLng a, LatLng b) {
        float[] results = new float[1];
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results);
        return results[0]; // en metros
    }


    private void obtenerUbicacionReal() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.obtenerUbicacionPorCollar(collarId).enqueue(new Callback<LocationModel>() {
            @Override
            public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LocationModel ubicacion = response.body();
                    double lat = ubicacion.getLatitud();
                    double lng = ubicacion.getLongitud();

                    Log.d("UbicacionReal", "Latitud: " + lat + ", Longitud: " + lng);

                    // Validamos que la ubicación no sea (0,0)
                    if (lat == 0.0 || lng == 0.0) {
                        Toast.makeText(MapActivity.this, "Ubicación inválida del collar (0,0)", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ubicacionCollar = new LatLng(lat, lng);

                    // Verifica zona segura
                    checkGeofence(ubicacionCollar);

                    // Mostrar marcador
                    if (petMarker != null) {
                        petMarker.setPosition(ubicacionCollar);
                    } else {
                        petMarker = mMap.addMarker(new MarkerOptions()
                                .position(ubicacionCollar)
                                .title("Ubicación del collar"));
                    }

                    // Mover cámara
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCollar, 16));
                } else {
                    Toast.makeText(MapActivity.this, "No se pudo obtener la ubicación del collar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LocationModel> call, Throwable t) {
                Log.e("Ubicacion", "Error: " + t.getMessage());
                Toast.makeText(MapActivity.this, "Error de red al obtener ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkGeofence(LatLng currentPosition) {
        float[] distance = new float[1];
        Location.distanceBetween(
                geofenceCenter.latitude, geofenceCenter.longitude,
                currentPosition.latitude, currentPosition.longitude,
                distance
        );

        if (distance[0] > geofenceRadius) {
            showNotification("Mascota fuera de zona segura", "El collar ha salido del área definida.");
            getUbicacionActual();
        }
    }

    private void getUbicacionActual() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && ubicacionCollar != null) {
                LatLng origen = new LatLng(location.getLatitude(), location.getLongitude());
                obtenerRutaHaciaCollar(origen, ubicacionCollar);
            }
        });
    }

    private void obtenerRutaHaciaCollar(LatLng origen, LatLng destino) {
        String originStr = origen.latitude + "," + origen.longitude;
        String destStr = destino.latitude + "," + destino.longitude;
        String apiKey = "TU_API_KEY_AQUI"; // Coloca aquí tu API Key de Google Directions

        ApiService apiService = RetrofitClient.getDirectionsInstance().create(ApiService.class);
        apiService.getDirections(originStr, destStr, apiKey).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject polyline = routes.getJSONObject(0).getJSONObject("overview_polyline");
                            String points = polyline.getString("points");
                            List<LatLng> decoded = decodePolyline(points);
                            mMap.addPolyline(new PolylineOptions().addAll(decoded).width(10).color(ContextCompat.getColor(MapActivity.this, R.color.purple_500)));
                        }
                    }
                } catch (Exception e) {
                    Log.e("Ruta", "Error al procesar JSON");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Ruta", "Error en solicitud: " + t.getMessage());
            }
        });
    }

    private void mostrarRecorridoDelCollar(int collarId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.obtenerRutaCollar(collarId).enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<LocationModel> puntos = response.body();
                    PolylineOptions polylineOptions = new PolylineOptions();

                    for (LocationModel punto : puntos) {
                        LatLng puntoMapa = new LatLng(punto.getLatitud(), punto.getLongitud());
                        polylineOptions.add(puntoMapa);
                    }

                    mMap.addPolyline(polylineOptions
                            .width(10)
                            .color(ContextCompat.getColor(MapActivity.this, R.color.colorAccent)));

                    Toast.makeText(MapActivity.this, "Recorrido mostrado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "No se encontraron puntos para el recorrido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error al obtener el recorrido: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length(), lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            poly.add(new LatLng(lat / 1E5, lng / 1E5));
        }
        return poly;
    }

    private void drawGeofenceCircle(LatLng center, float radius) {
        if (geofenceCircle != null) geofenceCircle.remove();
        geofenceCircle = mMap.addCircle(new CircleOptions().center(center).radius(radius).strokeColor(0xFFFF0000).fillColor(0x22FF0000).strokeWidth(2f));
    }

    private void openGeofenceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemeOverlay_AppPet_AlertDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_geofence, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText etLat = dialogView.findViewById(R.id.etLat);
        EditText etLng = dialogView.findViewById(R.id.etLng);
        EditText etRadius = dialogView.findViewById(R.id.etRadius);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);

        etLat.setText(String.valueOf(geofenceCenter.latitude));
        etLng.setText(String.valueOf(geofenceCenter.longitude));
        etRadius.setText(String.valueOf(geofenceRadius));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            try {
                double lat = Double.parseDouble(etLat.getText().toString());
                double lng = Double.parseDouble(etLng.getText().toString());
                float rad = Float.parseFloat(etRadius.getText().toString());
                geofenceCenter = new LatLng(lat, lng);
                geofenceRadius = rad;
                drawGeofenceCircle(geofenceCenter, geofenceRadius);
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1001);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Geofence Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal de notificaciones para geocercas");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permiso no concedido, salir sin mostrar notificación
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pet_placeholder)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            Log.e("Notificación", "No se pudo mostrar notificación: " + e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // 👈 importante

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show();
                obtenerUbicacionReal();  // Llamar después de que el permiso es otorgado
            } else {
                Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

