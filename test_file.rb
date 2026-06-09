
class Car

  attr_accessor :make, :model, :year
  def initialize(make, model, year)
    @make = make
    @model = model
    @year = year
  end
  def details
    "This is a #{year} #{@make} #{@model}"
  end
  def show
    # BAD: Direct string interpolation into SQL
    @user = User.where("username = '#{params[:username]}'")
  end
    # VIEW CONTEXT (ERB)
  <p>User Bio: <%= @user.bio.html_safe %></p>

  <p>Comment: <%= raw(params[:comment]) %></p>
  def update
    @user = User.find(params[:id])
    @user.update(params[:user].permit!)
  en
  def load_session_data
    user_data = Marshal.load(Base64.decode64(params[:backup_data]))
    @current_user_settings = user_data[:settings]
  en
  def optimize_image
    system("convert public/uploads/#{params[:filename]} -resize 50% public/uploads/thumb.png")
  end
  

end

my_car = Car.new("Toyota", "Corolla", 2021)
puts my_car.details 