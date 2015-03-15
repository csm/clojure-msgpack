(ns msgpack.streaming-test
  (:require [clojure.test :refer :all]
            [msgpack.streaming :refer [pack]]))

(defn- byte-literals
  [bytes]
  (map unchecked-byte bytes))


(defmacro packable [thing bytes]
  `(let [thing# ~thing
         bytes# (byte-literals ~bytes)]
     (is (= bytes# (pack thing#)))))

(deftest nil-test
  (testing "nil"
    (packable nil [0xc0])))

(deftest boolean-test
  (testing "booleans"
    (packable false [0xc2])
    (packable true [0xc3])))

(deftest int-test
  (testing "positive fixnum"
    (packable 0 [0x00])
    (packable 0x10 [0x10])
    (packable 0x7f [0x7f]))
  (testing "negative fixnum"
    (packable -1 [0xff])
    (packable -16 [0xf0])
    (packable -32 [0xe0]))
  (testing "uint 8"
    (packable 0x80 [0xcc 0x80])
    (packable 0xf0 [0xcc 0xf0])
    (packable 0xff [0xcc 0xff]))
  (testing "uint 16"
    (packable 0x100 [0xcd 0x01 0x00])
    (packable 0x2000 [0xcd 0x20 0x00])
    (packable 0xffff [0xcd 0xff 0xff]))
  (testing "uint 32"
    (packable 0x10000 [0xce 0x00 0x01 0x00 0x00])
    (packable 0x200000 [0xce 0x00 0x20 0x00 0x00])
    (packable 0xffffffff [0xce 0xff 0xff 0xff 0xff]))
  (testing "uint 64"
    (packable 0x100000000 [0xcf 0x00 0x00 0x00 0x01 0x00 0x00 0x00 0x00])
    (packable 0x200000000000 [0xcf 0x00 0x00 0x20 0x00 0x00 0x00 0x00 0x00])
    (packable 0xffffffffffffffff [0xcf 0xff 0xff 0xff 0xff 0xff 0xff 0xff 0xff]))
  (testing "int 8"
    (packable -33 [0xd0 0xdf])
    (packable -100 [0xd0 0x9c])
    (packable -128 [0xd0 0x80]))
  (testing "int 16"
    (packable -129 [0xd1 0xff 0x7f])
    (packable -2000 [0xd1 0xf8 0x30])
    (packable -32768 [0xd1 0x80 0x00]))
  (testing "int 32"
    (packable -32769 [0xd2 0xff 0xff 0x7f 0xff])
    (packable -1000000000 [0xd2 0xc4 0x65 0x36 0x00])
    (packable -2147483648 [0xd2 0x80 0x00 0x00 0x00]))
  (testing "int 64"
    (packable -2147483649 [0xd3 0xff 0xff 0xff 0xff 0x7f 0xff 0xff 0xff])
    (packable -1000000000000000002 [0xd3 0xf2 0x1f 0x49 0x4c 0x58 0x9b 0xff 0xfe])
    (packable -9223372036854775808 [0xd3 0x80 0x00 0x00 0x00 0x00 0x00 0x00 0x00])))
