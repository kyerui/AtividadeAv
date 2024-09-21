package com.example.atividadeav.model

class Estoque {
    companion object {
        private val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            listaProdutos.add(produto)
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.quantidade }
        }

        fun listaProdutos(): List<Produto>{
            return listaProdutos.toList()
        }

        fun deletarProduto(produto: Produto){
            listaProdutos.remove(produto)
        }

        fun calcularQuantidadeTotalProdutos(): Int{
            return listaProdutos.sumOf { it.quantidade }
        }
    }
}