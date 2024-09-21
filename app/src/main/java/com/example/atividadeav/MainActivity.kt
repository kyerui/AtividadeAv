package com.example.atividadeav

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        composable("lista/{produtosJson}") { backStackEntry ->
            val produtosJson = backStackEntry.arguments?.getString("produtosJson")
            if (produtosJson != null) {
                val produtosArray = Gson().fromJson(produtosJson, Array<String>::class.java)
                DetalheProduto(navController, produtosArray)
            }
        }
        composable("detalheproduto/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            if (produtoJson != null) {
                val contato = Gson().fromJson(produtoJson, Produto::class.java)
                DetalheProduto(navController, contato)
            }
        }
    }
}

@Composable
fun DetalheProduto(navController: NavHostController, initialProdutos: List<Produto>) {
    var produtos by remember { mutableStateOf(initialProdutos) }
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(produtos) { produto ->
            ProdutoItem(produto, navController = navController) {
                produtos = produtos.filter { it != produto }
                Toast.makeText(context, "Produto excluído", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
fun ProdutoItem(produto: Produto, navController: NavController, onDeleteClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        Text(text = "${produto.nome} - ${produto.quantidade}", fontSize = 20.sp,
            modifier = Modifier.clickable {
                val produtoJson = Gson().toJson(produto)
                navController.navigate("detalheproduto/$produtoJson")
            })
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onDeleteClick) {
            Text(text = "X")
        }
    }
}

@Composable
fun AddProduto(navController: NavHostController) {
    var produtos by remember { mutableStateOf(listOf<Produto>()) }
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
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
                }else{
                    produtos = produtos + Produto(
                        nome = nome,
                        quantidade = quantidade.toInt(),
                        categoria = categoria,
                        preco = preco.toFloat()
                    )
                    Toast.makeText(context, "Produto Cadastrado", Toast.LENGTH_SHORT).show()

                    nome = ""
                    quantidade = ""
                    categoria = ""
                    preco = ""
                }
            })
                {
            Text(text = "Cadastrar Produto")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            onClick = {

            }){
            Text("Produtos Cadastrados")
        }
    }
}

@Composable
fun ListaProdutos(navController: NavHostController, produtos: List<Produto>) {

}



@Preview(showBackground = true)
@Composable
fun Preview() {
    LayoutMain()
}