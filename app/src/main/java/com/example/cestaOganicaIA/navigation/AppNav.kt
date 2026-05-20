package com.example.cestaOganicaIA.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cestaOganicaIA.ui.gestion.GestionPerfilScreen
import com.example.cestaOganicaIA.ui.gestion.GestionUsuarioScreen
import com.example.cestaOganicaIA.ui.auth.RecuperarContrasenaScreen
import com.example.cestaOganicaIA.ui.auth.LoginScreen
import com.example.cestaOganicaIA.ui.auth.RegistrarseScreen
import com.example.cestaOganicaIA.view.BlockScreen
import com.example.cestaOganicaIA.view.DrawerMenu
import com.example.cestaOganicaIA.view.HistorialPedidosScreen
import com.example.cestaOganicaIA.view.ProductoFormScreen
import com.example.cestaOganicaIA.view.QrScannerScreen
import com.example.cestaOganicaIA.viewmodel.QrViewModel
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes

@Composable
fun AppNav(hasCameraPermission: Boolean, onRequestPermission: () -> Unit) {
    val navController = rememberNavController()
    val drawerMenuViewModel: DrawerMenuViewModel = viewModel()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(AppRoutes.REGISTRO) {
            RegistrarseScreen(navController = navController)
        }
        composable(AppRoutes.RECUPERAR_CLAVE) {
            RecuperarContrasenaScreen(navController = navController)
        }

        composable(
            route = "DrawerMenu/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val usernameArg = backStackEntry.arguments?.getString("username") ?: ""
            DrawerMenu(
                username = usernameArg,
                navController = navController,
                viewModel = drawerMenuViewModel
            )
        }
        
        // Alias para compatibilidad con AppRoutes.CATALOGO si es necesario
        composable(AppRoutes.CATALOGO) {
            val username = SessionManager.currentUser?.usuario ?: "invitado"
            navController.navigate("DrawerMenu/$username") {
                popUpTo(AppRoutes.LOGIN) { inclusive = true }
            }
        }

        composable(
            route = "ProductoFormScreen/{nombre}/{precio}/{descripcion}/{stock}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("precio") { type = NavType.StringType },
                navArgument("descripcion") { type = NavType.StringType },
                navArgument("stock") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
            val precio = backStackEntry.arguments?.getString("precio") ?: ""
            val descripcion = Uri.decode(backStackEntry.arguments?.getString("descripcion") ?: "")
            val stock = backStackEntry.arguments?.getInt("stock") ?: 0

            ProductoFormScreen(
                navController = navController,
                nombre = nombre,
                precio = precio,
                descripcion = descripcion,
                stock = stock
            )
        }

        composable(AppRoutes.QR_SCANNER) {
            val qrViewModel: QrViewModel = viewModel()
            QrScannerScreen(
                viewModel = qrViewModel,
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = onRequestPermission
            )
        }

        composable(AppRoutes.PERFIL) {
            GestionPerfilScreen(navController = navController)
        }

        composable(AppRoutes.HISTORIAL) {
            HistorialPedidosScreen(
                username = SessionManager.currentUser?.usuario ?: "",
                navController = navController,
                viewModel = drawerMenuViewModel
            )
        }

        composable(AppRoutes.ADMIN_USUARIOS) {
            GestionUsuarioScreen(navController = navController)
        }

        composable("block") {
            BlockScreen()
        }

        composable(AppRoutes.CARRITO) {
            SimpleStub("Pantalla: Próximamente carrito de compras")
        }
    }
}

@Composable
private fun SimpleStub(texto: String) {
    Text(text = texto, modifier = Modifier.padding(24.dp))
}
