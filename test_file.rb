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

  # CONTROLLER CONTEXT
  def show
    # BAD: Direct string interpolation into SQL
    @user = User.where("username = '#{params[:username]}'")
  end
    # VIEW CONTEXT (ERB)
  <p>User Bio: <%= @user.bio.html_safe %></p>

  <p>Comment: <%= raw(params[:comment]) %></p>

  # CONTROLLER CONTEXT
  def update
    @user = User.find(params[:id])
    
    # BAD: .permit! allows EVERYTHING passed in the request to be updated.
    # If an attacker sends { user: { is_admin: true } }, they elevate privileges.
    @user.update(params[:user].permit!)
  end
  # CONTROLLER CONTEXT
  def load_session_data
    # BAD: Parsing base64-encoded Marshal data straight from a cookie.
    # This can trigger immediate RCE if a malicious gadget chain is supplied.
    user_data = Marshal.load(Base64.decode64(params[:backup_data]))
    @current_user_settings = user_data[:settings]
  end
  # CONTROLLER CONTEXT
  def optimize_image
    # BAD: Single string execution allows shell metacharacters like ; or &&
    # If params[:filename] is "image.png; rm -rf /", the second command executes.
    system("convert public/uploads/#{params[:filename]} -resize 50% public/uploads/thumb.png")
  end
  

end

# Create an instance of the Car class
my_car = Car.new("Toyota", "Corolla", 2021)

# Call methods on the instance
puts my_car.details # Output: This is a 2021 Toyota Corolla