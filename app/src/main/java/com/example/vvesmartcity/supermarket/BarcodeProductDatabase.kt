package com.example.vvesmartcity.supermarket

data class BarcodeProduct(
    val barcode: String,
    val name: String,
    val price: Float,
    val category: String,
    val brand: String = ""
)

object BarcodeProductDatabase {
    private val products = mapOf(
        "6901234567890" to BarcodeProduct("6901234567890", "有机纯牛奶 250ml", 12.5f, "乳制品", "伊利"),
        "6901234567891" to BarcodeProduct("6901234567891", "全麦面包 400g", 8.0f, "烘焙食品", "桃李"),
        "6901234567892" to BarcodeProduct("6901234567892", "红富士苹果 1kg", 15.8f, "水果", "佳农"),
        "6901234567893" to BarcodeProduct("6901234567893", "进口橄榄油 500ml", 68.0f, "调味品", "欧丽薇兰"),
        "6901234567894" to BarcodeProduct("6901234567894", "鲜鸡蛋 30枚", 25.0f, "禽蛋", "德青源"),
        "6901234567895" to BarcodeProduct("6901234567895", "挪威三文鱼 200g", 88.0f, "海鲜", "挪威冰鲜"),
        "6901234567896" to BarcodeProduct("6901234567896", "农夫山泉矿泉水 550ml", 2.0f, "饮料", "农夫山泉"),
        "6901234567897" to BarcodeProduct("6901234567897", "康师傅红烧牛肉面", 4.5f, "方便食品", "康师傅"),
        "6901234567898" to BarcodeProduct("6901234567898", "旺旺雪饼 150g", 12.0f, "零食", "旺旺"),
        "6901234567899" to BarcodeProduct("6901234567899", "海天酱油 500ml", 15.0f, "调味品", "海天"),
        "6920734800101" to BarcodeProduct("6920734800101", "可口可乐 330ml", 3.0f, "饮料", "可口可乐"),
        "6920734800102" to BarcodeProduct("6920734800102", "百事可乐 330ml", 3.0f, "饮料", "百事"),
        "6901939621103" to BarcodeProduct("6901939621103", "统一冰红茶 500ml", 3.5f, "饮料", "统一"),
        "6902083891279" to BarcodeProduct("6902083891279", "蒙牛纯牛奶 250ml", 3.5f, "乳制品", "蒙牛"),
        "6903148040215" to BarcodeProduct("6903148040215", "奥利奥原味饼干 116g", 9.9f, "零食", "奥利奥"),
        "6924187221089" to BarcodeProduct("6924187221089", "乐事薯片原味 70g", 7.5f, "零食", "乐事"),
        "6901028075317" to BarcodeProduct("6901028075317", "双汇火腿肠 100g", 3.0f, "肉制品", "双汇"),
        "6901207100006" to BarcodeProduct("6901207100006", "思念水饺 500g", 18.0f, "冷冻食品", "思念"),
        "6902361900050" to BarcodeProduct("6902361900050", "湾仔码头水饺 400g", 22.0f, "冷冻食品", "湾仔码头"),
        "6903488500069" to BarcodeProduct("6903488500069", "金龙鱼调和油 1.8L", 45.0f, "调味品", "金龙鱼"),
        "6904123800005" to BarcodeProduct("6904123800005", "鲁花花生油 1L", 35.0f, "调味品", "鲁花"),
        "6905189200008" to BarcodeProduct("6905189200008", "太太乐鸡精 200g", 12.0f, "调味品", "太太乐"),
        "6906239200004" to BarcodeProduct("6906239200004", "老干妈豆豉酱 280g", 12.0f, "调味品", "老干妈"),
        "6907158300007" to BarcodeProduct("6907158300007", "王致和腐乳 340g", 8.0f, "调味品", "王致和"),
        "6908234500001" to BarcodeProduct("6908234500001", "恒顺香醋 500ml", 10.0f, "调味品", "恒顺"),
        "6909345600002" to BarcodeProduct("6909345600002", "李锦记生抽 500ml", 18.0f, "调味品", "李锦记"),
        "6910456700003" to BarcodeProduct("6910456700003", "海天蚝油 700g", 15.0f, "调味品", "海天"),
        "6911567800004" to BarcodeProduct("6911567800004", "乌江榨菜 100g", 2.5f, "调味品", "乌江"),
        "6912678900005" to BarcodeProduct("6912678900005", "涪陵榨菜 150g", 3.0f, "调味品", "涪陵"),
        "6913789000006" to BarcodeProduct("6913789000006", "卫龙辣条 106g", 5.0f, "零食", "卫龙")
    )
    
    fun findByBarcode(barcode: String): BarcodeProduct? {
        return products[barcode]
    }
    
    fun searchByName(query: String): List<BarcodeProduct> {
        if (query.isBlank()) return emptyList()
        return products.values.filter { 
            it.name.contains(query, ignoreCase = true) ||
            it.brand.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true)
        }
    }
    
    fun getAllProducts(): List<BarcodeProduct> = products.values.toList()
    
    fun generateRandomBarcode(): String {
        val existingBarcodes = products.keys.toList()
        return existingBarcodes.random()
    }
}
