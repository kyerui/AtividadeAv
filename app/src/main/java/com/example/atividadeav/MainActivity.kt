package com.example.atividadeav

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atividadeav.model.Estoque
import com.example.atividadeav.model.Produto
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutMain()
        }
    }
}

@Composable
fun LayoutMain() {
    val navController = rememberNavController()

    // NavHost define as rotas e o ponto de entrada
    NavHost(navController = navController, startDestination = "addestoque") {
        composable("addestoque") { AddProduto(navController) }
        composable("lista") { ListaProdutos(navController) }
        composable("detalheproduto/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            if (produtoJson != null) {
                val produto = Gson().fromJson(produtoJson, Produto::class.java)
                DetalheProduto(navController, produto)
            }
        }
        composable("estatistica/{valorTotal}/{totalEstoque}") { backStackEntry ->
            Estatisticas(navController, backStackEntry.arguments?.getString("valorTotal")?.toDouble(), backStackEntry.arguments?.getString("totalEstoque")?.toInt()
            )
        }
    }
}

@Composable
fun DetalheProduto(navController: NavHostController, produto: Produto) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Centraliza o conteúdo
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nome: ${produto.nome}\nQuantidade: ${produto.quantidade}\nCategoria: ${produto.categoria}\nPreço: ${produto.preco}",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.popBackStack()
            }) {
                Text("Voltar")
            }
        }
    }
}

@Composable
fun AddProduto(navController: NavHostController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        // Campo de texto para nome
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(70.dp),
            value = nome,
            onValueChange = { nome = it },
            label = { Text(text = "Nome do Produto:") },
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Campo de texto para nome
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(70.dp),
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text(text = "Categoria do Produto:") },
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Campo de texto para quantidade
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(70.dp),
            value = preco,
            onValueChange = { preco = it },
            label = { Text(text = "Preço: ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Campo de texto para quantidade
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(70.dp),
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text(text = "Quantidade: ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(10.dp))


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            onClick = {
                if (nome.isEmpty() || quantidade.isEmpty() || categoria.isEmpty() || preco.isEmpty()){
                    Toast.makeText(context, "Preencha corretamente!", Toast.LENGTH_SHORT).show()
                } else if (quantidade.toInt() <= 0 || preco.toFloat() <= 0) {
                        Toast.makeText(context, "Quantidade e preço devem ser maiores que 0", Toast.LENGTH_SHORT).show()
                    } else {
                        val novoProduto = Produto(nome, categoria, preco.toDouble(), quantidade.toInt())
                        Estoque.adicionarProduto(novoProduto)
                        Toast.makeText(context, "Produto Cadastrado", Toast.LENGTH_SHORT).show()
                    }
                }){
            Text(text = "Cadastrar Produto")
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(modifier = Modifier.fillMaxWidth().height(70.dp), onClick = {
                navController.navigate("lista")
            }){
            Text("Produtos Cadastrados")
        }
    }
}

@Composable
fun ListaProdutos(navController: NavHostController) {
    val listaProdutos = Estoque.listaProdutos()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f) // Permite que a LazyColumn ocupe o espaço restante
        ) {
            items(listaProdutos) { produto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${produto.nome} - (${produto.quantidade})")

                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto) // Serialize o produto para JSON
                        navController.navigate("detalheproduto/$produtoJson")
                    }){
                        Text(text = "Detalhes")
                    }
                }
            }
        }

        // Botão Voltar
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Voltar")
        }

        // Botão Ver Estatísticas
        Button(onClick = {
            val valorTotal = Estoque.calcularValorTotalEstoque()
            val quantidadeTotal = Estoque.calcularQuantidadeTotalProdutos()
            navController.navigate("estatistica/${valorTotal}/${quantidadeTotal}") // Navegando com valores
        }) {
            Text("Ver Estatísticas")
        }
    }
}

@Composable
fun Estatisticas(navController: NavHostController, valorTotal: Double?, quantidadeTotal: Int?) {

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        Text(text = "Estatísticas do Estoque", fontSize = 25.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Valor Total do Estoque: $valorTotal")
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Quantidade Total de Produtos: $quantidadeTotal")
        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Lista de Produtos")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    LayoutMain()
}