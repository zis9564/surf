# surf
review task for Surf. 

branch "main" contains source (unrefactored) code with my comments.\
branch "reviewed" contains refactored code.

---

### Ответы на вопросы

1. Что такое **Deadlock**?

    **Deadlock** - это взаимная блокировка. когда у нас есть несколько потоков и каждый поток ожидает ресурс, принадлежащий другому потоку.\
    решение: использовать блок с timeout.

2. Что такое **Race condition**?

    **Race condition** - это когда один и тот же ресурс используется несколькими потоками одновременно и в зависимости от порядка действий каждого потока       может быть несколько возможных результатов.

3. Алгоритм какой сложности выполняется бытрее **O(log(n))** или **O(n)**?

    алгоритм **O(log(n))** выполняется быстрее чем **O(n)**.\
    - **O(log(n))** имеет алгоритмическое время выполнения. При увеличении размера input время выполнения будет расти незначительно.
    - **O(n)** имеет линейное время выполнения. При увеличении размера input время выполнения будет расти пропорционально.

4. Что такое **Транзакция**?

    **Транзакция** это набор последовательных операций с базой даннных. Понимание что такое транзакция начинается с ее свойств (ACID)\
    - **Atomicity** - транзакция выполнена полностью или не выполнена совсем
    - **Consistency** - транзакция фиксирует результаты сохраняя консистентность бд
    - **Isolation** - параллельные транзакции не должны оказывать влияние друг на друга
    - **Durability** - после завершения транзакции данные не могут быть отменены (исчезнуть)
