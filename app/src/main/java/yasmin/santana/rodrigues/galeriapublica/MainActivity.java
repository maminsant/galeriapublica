package yasmin.santana.rodrigues.galeriapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    public class MainViewModel extends AndroidViewModel{
        int navigationOpSelected = R.id.gridViewOp; //guarda a opção escolhida pelo usuário no menu btNav

        public MainViewModel(@NonNull Application application){
            super(application);
        }

        public int getNavigationOpSelected(){ //pega e seta o valor
            return navigationOpSelected;
        }

        public void setNavigationOpSelected(int navigationOpSelected){
          this.navigationOpSelected = navigationOpSelected;
        }
    }
    BottomNavigationView bottowNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        bottowNavigationView = findViewById(R.id.btNav);
        bottowNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() { // escutador” de eventos de seleção do menu --> toda vez que selecionar vai ser chamado onNavigation
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                vm.setNavigationOpSelected(item.getItemId());
                switch (item.getItemId()) {
                    case R.id.gridViewOp:
                        GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                        setFragment(gridViewFragment);
                        break;
                    case R.id.listViewOp:
                        ListViewFragment listViewFragment = ListViewFragment.newInstance();
                        setFragment(listViewFragment);
                        break;
                }
                return true;
            }
        }

        );
    }

    void setFragment(Fragment fragment){ //recebe um frag, seta e coloca para aparecer na tela -- faz parte da pilha de tela do botão voltar do Android
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void checkForPermissions(List<String> permissions){
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions){ //verifica as permissões
            if( !hasPermission(permission)){
                permissionsNotGranted.add(permission);
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsNotGranted.size() > 0){ //se não permitir, chama o método que criou a caixinhade aviso para falar que precisa permitir pra usar o app
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
        }
        else{
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class); //obtem opcao escolhida, seta em bottow navigation porque assim passsa a informação pra main
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottowNavigationView.setSelectedItemId(navigationOpSelected);
        }
    }
    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){ //ve se já deixaram usar
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION){ //ve se concedeu a permissão ou não
            for(String permission : permissions){
                if(!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }
        if(permissionsRejected.size() > 0){ //se é necessário uma permissão e o usuario nao da, mostra a msg que precisa dar a permissão
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar esse app é preciso conceder essas permissões.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //pede dnv
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
        else{
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottowNavigationView.setSelectedItemId(navigationOpSelected);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        checkForPermissions(permissions);
    }
}
