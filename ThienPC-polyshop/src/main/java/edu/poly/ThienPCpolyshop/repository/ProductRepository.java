package edu.poly.ThienPCpolyshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.ThienPCpolyshop.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
