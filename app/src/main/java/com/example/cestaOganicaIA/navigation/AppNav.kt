package com.example.cestaOganicaIA.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cestaOganicaIA.data.repository.CarritoRepository
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import com.example.cestaOganicaIA.data.repository.FavoritoRepository
import com.example.cestaOganicaIA.ui.auth.LoginScreen
import com.example.cestaOganicaIA.ui.auth.RegistroScreen
import com.example.cestaOganicaIA.ui.auth.RecuperarClaveScreen
import com.example.cestaOganicaIA.ui.gestion.GestionPerfilScreen
import com.example.cestaOganicaIA.ui.gestion.GestionUsuarioScreen
import com.example.cestaOganicaIA.ui.gestion.AdminPedidosScreen
import com.example.cestaOganicaIA.view.BlockScreen
import com.example.cestaOganicaIA.view.DrawerMenu
import com.example.cestaOganicaIA.view.HistorialPedidosScreen
import com.example.cestaOganicaIA.view.ProductoFormScreen
import com.example.cestaOganicaIA.view.QrScannerScreen
import com.example.cestaOganicaIA.view.CarritoScreen
import com.example.cestaOganicaIA.viewmodel.QrViewModel
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import com.example.cestaOganicaIA.viewmodel.CarritoViewModel
import com.example.cestaOganicaIA.viewmodel.HistorialViewModel
import com.example.cestaOganicaIA.viewmodel.AdminViewModel
import com.example.cestaOganicaIA.viewmodel.CatalogViewModel
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes

@Composable
fun AppNav(hasCameraPermission: Boolean, onRequestPermission: () -> Unit) {
    val navController = rememberNavController()
    
    // Repositorios Firebase
    val carritoRepo = CarritoRepository()
    val pedidoRepo = PedidoRepository()
    val favoritoRepo = FavoritoRepository()
    
    // ViewModels centralizados
    val drawerMenuViewModel: DrawerMenuViewModel = viewModel()
    
    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.Factory(carritoRepo, favoritoRepo)
    )
    
    val carritoViewModel: CarritoViewModel = viewModel(
        factory = CarritoViewModel.Factory(carritoRepo, pedidoRepo)
    )
    
    val historialViewModel: HistorialViewModel = viewModel(
        factory = HistorialViewModel.Factory(pedidoRepo)
    )
    
    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModel.Factory(pedidoRepo)
    )

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }
        
        composable(AppRoutes.REGISTRO) {
            RegistroScreen(navController = navController)
        }
        
        composable(AppRoutes.RECUPERAR_CLAVE) {
            RecuperarClaveScreen(navController = navController)
        }

        composable(AppRoutes.CATALOGO) {
            val user = SessionManager.currentUser
            LaunchedEffect(user) {
                user?.let { catalogViewModel.cargarDatosUsuario(it.uid) }
            }

            DrawerMenu(
                username = user?.usuario ?: "Invitado",
                navController = navController,
                viewModel = drawerMenuViewModel
            )
        }

        composable(
            route = "ProductoFormScreen/{nombre}/{precio}/{descripcion}/{stock}/{imagenResId}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("precio") { type = NavType.StringType },
                navArgument("descripcion") { type = NavType.StringType },
                navArgument("stock") { type = NavType.IntType },
                navArgument("imagenResId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
            val precio = backStackEntry.arguments?.getString("precio") ?: ""
            val descripcion = Uri.decode(backStackEntry.arguments?.getString("descripcion") ?: "")
            val stock = backStackEntry.arguments?.getInt("stock") ?: 0
            val imagenResId = backStackEntry.arguments?.getInt("imagenResId") ?: 0

            ProductoFormScreen(
                navController = navController,
                nombre = nombre,
                precio = precio,
                descripcion = descripcion,
                stock = stock,
                imagenResId = imagenResId,
                carritoViewModel = carritoViewModel
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
                navController = navController,
                viewModel = historialViewModel
            )
        }

        composable(AppRoutes.ADMIN_USUARIOS) {
            GestionUsuarioScreen(navController = navController)
        }

        composable(AppRoutes.ADMIN_PEDIDOS) {
            AdminPedidosScreen(
                navController = navController,
                viewModel = adminViewModel
            )
        }

        composable("block") {
            BlockScreen()
        }

        composable(AppRoutes.CARRITO) {
            CarritoScreen(
                navController = navController,
                carritoViewModel = carritoViewModel,
                drawerMenuViewModel = drawerMenuViewModel
            )
        }
    }
}
