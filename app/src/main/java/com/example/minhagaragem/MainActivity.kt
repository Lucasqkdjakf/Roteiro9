package com.example.minhagaragem

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView() class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContent {
                    // O nome do tema muda dependendo do nome que você deu ao projeto
                    SeuTemaTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppGaragem() // A mágica acontece aqui!
                        }
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}


data class Carro(val id: String = UUID.randomUUID().toString(), val modelo: String, val ano: String)
data class Peca(val id: String = UUID.randomUUID().toString(), val nome: String, val marca: String)


class GaragemViewModel : ViewModel() {

    var listaDeCarros = mutableStateListOf<Carro>()
    var listaDePecas = mutableStateListOf<Peca>()


    fun salvarCarro(id: String?, modelo: String, ano: String) {
        val carroExistente = listaDeCarros.find { it.id == id }
        if (carroExistente != null) {
            val index = listaDeCarros.indexOf(carroExistente)
            listaDeCarros[index] = Carro(id = carroExistente.id, modelo = modelo, ano = ano)
        } else {
            listaDeCarros.add(Carro(modelo = modelo, ano = ano))
        }
    }

    fun salvarPeca(id: String?, nome: String, marca: String) {
        val pecaExistente = listaDePecas.find { it.id == id }
        if (pecaExistente != null) {
            val index = listaDePecas.indexOf(pecaExistente)
            listaDePecas[index] = Peca(id = pecaExistente.id, nome = nome, marca = marca)
        } else {
            listaDePecas.add(Peca(nome = nome, marca = marca))
        }
    }
}


@Composable
fun ItemDoCarro(carro: Carro, aoClicar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { aoClicar() } // Requisito: clicar no item para editar
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Modelo: ${carro.modelo}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Ano: ${carro.ano}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ItemDaPeca(peca: Peca, aoClicar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { aoClicar() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Peça: ${peca.nome}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Marca: ${peca.marca}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun TelaFormularioCarro(viewModel: GaragemViewModel, navController: NavHostController, carroId: String?) {
    // Se recebemos um ID, procuramos o carro para editar. Se não, fica vazio.
    val carroParaEditar = viewModel.listaDeCarros.find { it.id == carroId }

    // Controle de estado do que o usuário está digitando
    var modelo by remember { mutableStateOf(carroParaEditar?.modelo ?: "") }
    var ano by remember { mutableStateOf(carroParaEditar?.ano ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo do Carro (ex: Parati)") }
        )
        OutlinedTextField(
            value = ano,
            onValueChange = { ano = it },
            label = { Text("Ano (ex: 1998)") }
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = {
                viewModel.salvarCarro(carroId, modelo, ano)
                navController.popBackStack() // Salva e volta
            }) {
                Text("Salvar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = {
                navController.popBackStack() // Requisito: Botão cancelar volta pra rota anterior
            }) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun TelaFormularioPeca(viewModel: GaragemViewModel, navController: NavHostController, pecaId: String?) {
    val pecaParaEditar = viewModel.listaDePecas.find { it.id == pecaId }
    var nome by remember { mutableStateOf(pecaParaEditar?.nome ?: "") }
    var marca by remember { mutableStateOf(pecaParaEditar?.marca ?: "") }

    // Mesma estrutura do carro, só mudam os textos!
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome da Peça") })
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca (ex: RCI)") })

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = {
                viewModel.salvarPeca(pecaId, nome, marca)
                navController.popBackStack()
            }) { Text("Salvar") }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Cancelar") }
        }
    }
}


@Composable
fun AppGaragem() {
    val navController = rememberNavController() // O motorista do GPS
    val viewModel: GaragemViewModel = viewModel() // Instanciando nosso cérebro

    // Scaffold é o esqueleto da tela. Ele já deixa espaço pra barra inferior.
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("🚗") },
                    label = { Text("Carros") },
                    selected = false,
                    onClick = { navController.navigate("lista_carros") }
                )
                NavigationBarItem(
                    icon = { Text("⚙️") },
                    label = { Text("Peças") },
                    selected = false,
                    onClick = { navController.navigate("lista_pecas") }
                )
            }
        }
    ) { paddingValues ->
        // O NavHost dita as rotas (os endereços de cada tela)
        NavHost(
            navController = navController,
            startDestination = "lista_carros",
            modifier = Modifier.padding(paddingValues)
        ) {

            // TELA 1: LISTA DE CARROS
            composable("lista_carros") {
                Column {
                    Button(onClick = { navController.navigate("form_carro/novo") }) {
                        Text("Adicionar Carro")
                    }
                    LazyColumn { // LazyColumn é a RecyclerView do Compose!
                        items(viewModel.listaDeCarros) { carro ->
                            ItemDoCarro(carro = carro, aoClicar = {
                                // Vai pra edição passando o ID
                                navController.navigate("form_carro/${carro.id}")
                            })
                        }
                    }
                }
            }

            // TELA 2: LISTA DE PEÇAS
            composable("lista_pecas") {
                Column {
                    Button(onClick = { navController.navigate("form_peca/novo") }) {
                        Text("Adicionar Peça")
                    }
                    LazyColumn {
                        items(viewModel.listaDePecas) { peca ->
                            ItemDaPeca(peca = peca, aoClicar = {
                                navController.navigate("form_peca/${peca.id}")
                            })
                        }
                    }
                }
            }

            // TELA 3: FORMULÁRIO DE CARRO
            composable("form_carro/{carroId}") { navBackStackEntry ->
                val carroId = navBackStackEntry.arguments?.getString("carroId")
                // Se for "novo", passamos null para criar um do zero
                val idReal = if (carroId == "novo") null else carroId
                TelaFormularioCarro(viewModel, navController, idReal)
            }

            // TELA 4: FORMULÁRIO DE PEÇA
            composable("form_peca/{pecaId}") { navBackStackEntry ->
                val pecaId = navBackStackEntry.arguments?.getString("pecaId")
                val idReal = if (pecaId == "novo") null else pecaId
                TelaFormularioPeca(viewModel, navController, idReal)
            }
        }
    }
}