package yasmin.santana.rodrigues.galeriapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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

}
