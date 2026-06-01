# Define a class named Car
class Car
  # Attributes (instance variables)
  attr_accessor :make, :model, :year

  # Constructor method
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end

  # Method to display information about the car
  def details
    "This is a #{year} #{@make} #{@model}"
  end
end

# Create an instance of the Car class
my_car = Car.new("Toyota", "Corolla", 2021)

# Call methods on the instance
puts my_car.details # Output: This is a 2021 Toyota Corolla