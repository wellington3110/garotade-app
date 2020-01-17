package br.com.wgmacedo.core.account

class AccountNumber {

     val value: String

    constructor(number: Int) {
        this.value = normalize(number.toString())
    }

    constructor(number: String) {
        this.value = normalize(number)
    }

    fun normalize(value: String) = value.padStart(6, '0')

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AccountNumber
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}