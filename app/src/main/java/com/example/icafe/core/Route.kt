package com.example.icafe.core

// Definición de todas las rutas de la aplicación
sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")

    // Sede Selection and Management
    object SedeSelection : Route("sede_selection/{portfolioId}") {
        fun createRoute(portfolioId: String) = "sede_selection/$portfolioId"
    }
    object AddEditSede : Route("add_edit_sede/{sedeId}") { // "new" for add, ID for edit
        fun createRoute(sedeId: String) = "add_edit_sede/$sedeId"
    }

    // Dashboard (New Home)
    object Dashboard : Route("dashboard/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "dashboard/$portfolioId/$selectedSedeId"
    }

    // Contacts Landing
    object ContactsLanding : Route("contacts_landing/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "contacts_landing/$portfolioId/$selectedSedeId"
    }

    // Rutas para Empleados
    object EmployeeList : Route("employee_list/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "employee_list/$portfolioId/$selectedSedeId"
    }
    object AddEmployee : Route("employee_add/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "employee_add/$portfolioId/$selectedSedeId"
    }
    object EmployeeDetail : Route("employee_detail/{portfolioId}/{selectedSedeId}/{employeeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, employeeId: Long) = "employee_detail/$portfolioId/$selectedSedeId/$employeeId"
    }
    object EditEmployee : Route("employee_edit/{portfolioId}/{selectedSedeId}/{employeeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, employeeId: Long) = "employee_edit/$portfolioId/$selectedSedeId/$employeeId"
    }

    // Rutas para Proveedores
    object ProviderList : Route("provider_list/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "provider_list/$portfolioId/$selectedSedeId"
    }
    object AddProvider : Route("provider_add/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "provider_add/$portfolioId/$selectedSedeId"
    }
    object ProviderDetail : Route("provider_detail/{portfolioId}/{selectedSedeId}/{providerId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, providerId: Long) = "provider_detail/$portfolioId/$selectedSedeId/$providerId"
    }
    object EditProvider : Route("provider_edit/{portfolioId}/{selectedSedeId}/{providerId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, providerId: Long) = "provider_edit/$portfolioId/$selectedSedeId/$providerId"
    }

    // Rutas para Inventario Landing
    object InventoryLanding : Route("inventory_landing/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "inventory_landing/$portfolioId/$selectedSedeId"
    }

    // Rutas para Insumos
    object ItemList : Route("item_list/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "item_list/$portfolioId/$selectedSedeId"
    }
    object AddItem : Route("item_add/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "item_add/$portfolioId/$selectedSedeId"
    }
    object ItemDetail : Route("item_detail/{portfolioId}/{selectedSedeId}/{itemId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, itemId: Long) = "item_detail/$portfolioId/$selectedSedeId/$itemId"
    }
    object EditItem : Route("item_edit/{portfolioId}/{selectedSedeId}/{itemId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, itemId: Long) = "item_edit/$portfolioId/$selectedSedeId/$itemId"
    }

    // Rutas para Movimientos de Inventario
    object InventoryMovements : Route("inventory_movements/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "inventory_movements/$portfolioId/$selectedSedeId"
    }

    // Rutas para Productos
    object ProductList : Route("product_list/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "product_list/$portfolioId/$selectedSedeId"
    }
    object AddProduct : Route("product_add/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "product_add/$portfolioId/$selectedSedeId"
    }
    object ProductDetail : Route("product_detail/{portfolioId}/{selectedSedeId}/{productId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, productId: Long) = "product_detail/$portfolioId/$selectedSedeId/$productId"
    }
    object EditProduct : Route("product_edit/{portfolioId}/{selectedSedeId}/{productId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, productId: Long) = "product_edit/$portfolioId/$selectedSedeId/$productId"
    }

    // Rutas para Finanzas (NUEVO)
    object FinanceLanding : Route("finance_landing/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "finance_landing/$portfolioId/$selectedSedeId"
    }
    object SalesList : Route("sales_list/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "sales_list/$portfolioId/$selectedSedeId"
    }
    object AddSale : Route("add_sale/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "add_sale/$portfolioId/$selectedSedeId"
    }
    object SalesDetail : Route("sales_detail/{portfolioId}/{selectedSedeId}/{saleId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, saleId: Long) = "sales_detail/$portfolioId/$selectedSedeId/$saleId"
    }
    object PurchaseOrderList : Route("purchase_order_list/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "purchase_order_list/$portfolioId/$selectedSedeId"
    }
    object AddPurchaseOrder : Route("add_purchase_order/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "add_purchase_order/$portfolioId/$selectedSedeId"
    }
    object PurchaseOrderDetail : Route("purchase_order_detail/{portfolioId}/{selectedSedeId}/{purchaseOrderId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String, purchaseOrderId: Long) = "purchase_order_detail/$portfolioId/$selectedSedeId/$purchaseOrderId"
    }

    // Placeholder para futuras características (estos serán reemplazados por implementaciones adecuadas más adelante)
    object Purchases : Route("purchases/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "purchases/$portfolioId/$selectedSedeId"
    }
    object Sales : Route("sales/{portfolioId}/{selectedSedeId}") {
        fun createRoute(portfolioId: String, selectedSedeId: String) = "sales/$portfolioId/$selectedSedeId"
    }
}